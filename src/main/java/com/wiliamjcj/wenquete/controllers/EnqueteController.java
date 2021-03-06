package com.wiliamjcj.wenquete.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiliamjcj.wenquete.dto.EnqueteDTO;
import com.wiliamjcj.wenquete.dto.OpcaoDTO;
import com.wiliamjcj.wenquete.messages.MsgUtil;
import com.wiliamjcj.wenquete.messages.ResponseMsg;
import com.wiliamjcj.wenquete.services.EnqueteService;
import com.wiliamjcj.wenquete.utils.APIResponse;
import com.wiliamjcj.wenquete.utils.ExceptionLogger;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "${rest.enquete.mapping}")
public class EnqueteController {

	@Autowired
	private EnqueteService enqueteService;

	@Autowired
	private MsgUtil messages;

	@Value("${rest.enquete.mapping}")
	private String BASE_ENDPOINT;

	@Autowired
	private ExceptionLogger elog;

	@ApiOperation(value = "Lista as enquetes que j� foram iniciadas.")
	@GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<APIResponse<Page<EnqueteDTO>>> listar(Pageable p) {
		try {
			Page<EnqueteDTO> enquetes = enqueteService.buscarEnquetes(p);
			APIResponse<Page<EnqueteDTO>> apiResponse = new APIResponse<Page<EnqueteDTO>>();
			apiResponse.setData(enquetes);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			elog.erro(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Adiciona uma nova enquete, retornando seu id e token.")
	@PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<APIResponse<String>> adicionarEnquete(@Valid @RequestBody EnqueteDTO enquete,
			BindingResult res) throws URISyntaxException, NoSuchAlgorithmException, UnsupportedEncodingException {
		try {
			APIResponse<String> apiResponse = new APIResponse<String>();

			if (res.hasErrors()) {
				res.getAllErrors().stream().forEach(err -> apiResponse.getErrors().add(err.getDefaultMessage()));
				return ResponseEntity.badRequest().body(apiResponse);
			}
			enquete = enqueteService.criarEnquete(enquete);
			URI location = new URI(BASE_ENDPOINT + "/" + enquete.getId());

			return ResponseEntity.created(location).header("token", enquete.getToken()).body(apiResponse);
		} catch (Exception e) {
			elog.erro(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Busca uma enquete por id.")
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Object> buscarEnquete(@PathVariable(name = "id", required = true) Long id,
			@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
		try {
			APIResponse<EnqueteDTO> apiResponse = new APIResponse<EnqueteDTO>();
			ResponseMsg erro = null;

			EnqueteDTO enquete = enqueteService.buscarEnquete(id);
			apiResponse.setData(enquete);

			erro = null != enquete.getId() ? erro : ResponseMsg.ENQUETE_CNTRL_ENQUETE_NOCONTENT;

			if (null == erro) {
				return ResponseEntity.ok(apiResponse);
			} else {
				apiResponse.getErrors().add(messages.msg(erro, locale));
				return ResponseEntity.status(erro.getStatus()).body(apiResponse);
			}
		} catch (Exception e) {
			elog.erro(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Inicia uma enquete, permitindo assim a vota��o. Necess�rio informar o id e o token. ")
	@PatchMapping(value = "/{id}/iniciar", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<APIResponse<String>> iniciarEnquete(@PathVariable(name = "id", required = true) Long id,
			@RequestHeader(value = "token", required = true) String token,
			@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
		try {
			APIResponse<String> apiResponse = new APIResponse<String>();
			ResponseMsg msgRes = ResponseMsg.ENQUETE_CNTRL_INICIAR_SUCESSO;

			EnqueteDTO enquete = enqueteService.buscarEnquete(id);

			msgRes = null != enquete.getId() ? msgRes : ResponseMsg.ENQUETE_CNTRL_ENQUETE_NOCONTENT;
			msgRes = token.equals(enquete.getToken()) ? msgRes : ResponseMsg.ENQUETE_CNTRL_TOKEN_INVALIDO;
			msgRes = !enquete.isEncerrada() ? msgRes : ResponseMsg.ENQUETE_CNTRL_ENCERRADA;

			apiResponse.setData(messages.msg(msgRes, locale));

			if (msgRes.equals(ResponseMsg.ENQUETE_CNTRL_INICIAR_SUCESSO)) {
				enquete.setIniciada(true);
				enqueteService.atualizarEnquete(enquete);
				return ResponseEntity.ok(apiResponse);
			} else {
				return ResponseEntity.status(msgRes.getStatus()).body(apiResponse);
			}
		} catch (Exception e) {
			elog.erro(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Vota em uma das op��es da enquete, informando o id da enquete e o id da op��o desejada.")
	@PostMapping(value = "/{id}/{idOpcao}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<APIResponse<EnqueteDTO>> votarEnquete(@PathVariable(name = "id", required = true) Long id,
			@PathVariable(name = "idOpcao", required = true) Long idOpcao,
			@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
		try {
			APIResponse<EnqueteDTO> apiResponse = new APIResponse<EnqueteDTO>();
			ResponseMsg msgErro = null;

			EnqueteDTO enquete = enqueteService.buscarEnquete(id);

			msgErro = null != enquete.getId() ? msgErro : ResponseMsg.ENQUETE_CNTRL_ENQUETE_NOCONTENT;
			msgErro = enquete.isIniciada() && !enquete.isEncerrada() ? msgErro : ResponseMsg.ENQUETE_CNTRL_INATIVA;

			Optional<OpcaoDTO> opcao = enquete.getOpcoes().stream().filter(op -> op.getId().equals(idOpcao))
					.findFirst();

			msgErro = opcao.isPresent() ? msgErro : ResponseMsg.ENQUETE_CNTRL_OPCAO_NOCONTENT;

			if (null == msgErro) {
				opcao.get().votar();
				enquete = enqueteService.atualizarEnquete(enquete);
			} else {
				apiResponse.getErrors().add(messages.msg(msgErro, locale));
				return ResponseEntity.status(msgErro.getStatus()).body(apiResponse);
			}

			apiResponse.setData(enquete);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			elog.erro(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Encerra uma enquete, finalizando assim a vota��o. Necess�rio informar o id e o token.")
	@PatchMapping(value = "/{id}/terminar", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<APIResponse<EnqueteDTO>> terminarEnquete(@PathVariable(name = "id", required = true) Long id,
			@RequestHeader(value = "token", required = true) String token,
			@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
		try {
			APIResponse<EnqueteDTO> apiResponse = new APIResponse<EnqueteDTO>();
			ResponseMsg erroMsg = null;

			EnqueteDTO enquete = enqueteService.buscarEnquete(id);

			erroMsg = null != enquete.getId() ? erroMsg : ResponseMsg.ENQUETE_CNTRL_ENQUETE_NOCONTENT;
			erroMsg = token.equals(enquete.getToken()) ? erroMsg : ResponseMsg.ENQUETE_CNTRL_TOKEN_INVALIDO;

			if (null == erroMsg) {
				enquete.setEncerrada(true);
				enquete = enqueteService.atualizarEnquete(enquete);
				apiResponse.setData(enquete);
				return ResponseEntity.ok(apiResponse);
			} else {
				apiResponse.getErrors().add(messages.msg(erroMsg, locale));
				return ResponseEntity.status(erroMsg.getStatus()).body(apiResponse);
			}
		} catch (Exception e) {
			elog.erro(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@ApiOperation(value = "Deleta uma enquete atrav�s do id e do token gerado na inser��o da mesma.")
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<APIResponse<EnqueteDTO>> deletarEnquete(@PathVariable(name = "id", required = true) Long id,
			@RequestHeader(value = "token", required = true) String token,
			@RequestHeader(name = "Accept-Language", required = false) Locale locale) {
		try {
			APIResponse<EnqueteDTO> apiResponse = new APIResponse<EnqueteDTO>();
			ResponseMsg erroMsg = null;

			EnqueteDTO enquete = enqueteService.buscarEnquete(id);

			erroMsg = null != enquete.getId() ? erroMsg : ResponseMsg.ENQUETE_CNTRL_ENQUETE_NOCONTENT;
			erroMsg = token.equals(enquete.getToken()) ? erroMsg : ResponseMsg.ENQUETE_CNTRL_TOKEN_INVALIDO;

			if (null == erroMsg) {
				apiResponse.setData(enquete);
				enqueteService.deletarEnquete(enquete);
				return ResponseEntity.ok(apiResponse);
			} else {
				apiResponse.getErrors().add(messages.msg(erroMsg, locale));
				return ResponseEntity.status(erroMsg.getStatus()).body(apiResponse);
			}
		} catch (Exception e) {
			elog.erro(e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
