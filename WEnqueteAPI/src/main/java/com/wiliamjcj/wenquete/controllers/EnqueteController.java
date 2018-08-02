package com.wiliamjcj.wenquete.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
import com.wiliamjcj.wenquete.utils.APIResponse;

@RestController
@RequestMapping(value = "/api/enquetes")
public class EnqueteController {

	private final Logger log = LoggerFactory.getLogger(EnqueteController.class);
	
	@GetMapping
	public ResponseEntity<APIResponse<List<EnqueteDTO>>> listar() {

		APIResponse<List<EnqueteDTO>> apiResponse = new APIResponse<List<EnqueteDTO>>();
		apiResponse.setData(new ArrayList<EnqueteDTO>());
		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping
	public ResponseEntity<String> adicionarEnquete(@RequestBody String enquete) throws URISyntaxException {
		long id = 1l;
		String token = "123";
		String uri = "/api/enquetes/"+id;
		URI location = new URI(uri);
		
		return ResponseEntity.created(location).header("token", token).build();
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<APIResponse<EnqueteDTO>> buscarEnquete(@PathVariable("id") Long id) {
		APIResponse<EnqueteDTO> apiResponse = new APIResponse<EnqueteDTO>();
		EnqueteDTO enquete = new EnqueteDTO();
		enquete.getOpcoes().add(new OpcaoDTO());
		apiResponse.setData(enquete);
		return ResponseEntity.ok(apiResponse);
	}

	@PatchMapping(value = "/{id}/iniciar")
	public ResponseEntity<APIResponse<String>> iniciarEnquete(@PathVariable("id") Long id,
			@RequestHeader(value = "token") String token) {
		APIResponse<String> apiResponse = new APIResponse<String>();
		apiResponse.setData("Enquete id:"+id+" iniciada com sucesso!");
		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping(value = "/{id}")
	public ResponseEntity<APIResponse<String>> votarEnquete(@PathVariable("id") Long id, @RequestBody String opcao) {
		APIResponse<String> apiResponse = new APIResponse<String>();
		apiResponse.setData("");
		return ResponseEntity.ok(apiResponse);
	}

	@PatchMapping(value = "/{id}/terminar")
	public ResponseEntity<APIResponse<String>> terminarEnquete(@PathVariable("id") Long id,
			@RequestHeader(value = "token") String token) {
		APIResponse<String> apiResponse = new APIResponse<String>();
		apiResponse.setData("");
		return ResponseEntity.ok(apiResponse);
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<APIResponse<List<EnqueteDTO>>> deletarEnquete(@PathVariable("id") Long id) {
		APIResponse<List<EnqueteDTO>> apiResponse = new APIResponse<List<EnqueteDTO>>();
		apiResponse.setData(new ArrayList<EnqueteDTO>());
		return ResponseEntity.ok(apiResponse);
	}
}
