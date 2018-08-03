package com.wiliamjcj.wenquete.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiliamjcj.wenquete.controllers.EnqueteController;
import com.wiliamjcj.wenquete.dto.EnqueteDTO;
import com.wiliamjcj.wenquete.dto.OpcaoDTO;
import com.wiliamjcj.wenquete.utils.APIResponse;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(value = EnqueteController.class, secure = false)
public class EnqueteAPITests {

	@Autowired
	private MockMvc mockMvc;
	private String BASE_ENDPOINT = "/api/enquetes/";
	private String TESTE_TOKEN = "ABC123";

	@Test
	public void adicionarEnqueteSucesso() throws Exception {
		EnqueteDTO enqueteTeste = criarEnquete(null, null);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.content(enqueteTeste.toJson())
				.contentType(MediaType.APPLICATION_JSON_UTF8);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());

		assertThat(result.getResponse().getHeader("location")).isNotBlank();

		assertThat(result.getResponse().getHeader("token")).isNotBlank();
	}

	@Test
	public void listarTodasEnquetesSucesso() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON_UTF8);
		
		String json = verificarStatusOk(requestBuilder);
		APIResponse<List<EnqueteDTO>> enquetes = getEnquetesFromResponse(json);

		assertThat(enquetes).isNotNull();

		assertThat(enquetes.getData()).isNotNull();

		assertTrue(enquetes.getData().stream()
			.allMatch(enquete -> !StringUtils.isEmpty(enquete.getPergunta()) 
					&& !enquete.getOpcoes().isEmpty()
					&& enquete.getOpcoes().stream()
						.allMatch(opcao -> null != opcao && null != opcao.getId())
			)
		);
	}

	@Test
	public void buscarEnqueteSucesso() throws Exception {

		EnqueteDTO enqueteTeste = criarEnquete(2L, TESTE_TOKEN);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteTeste.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		String json = verificarStatusOk(requestBuilder);
		APIResponse<EnqueteDTO> apiResponse = getEnqueteFromResponse(json);

		assertEquals(enqueteTeste.toJson(), apiResponse.getData().toJson());
	}

	@Test
	public void iniciarEnqueteSucesso() throws Exception {
		EnqueteDTO enqueteTeste = criarEnquete(3L, TESTE_TOKEN);
		URI uri = new URI(BASE_ENDPOINT + enqueteTeste.getId() + "/iniciar");

		RequestBuilder requestBuilder = MockMvcRequestBuilders.patch(uri)
				.header("token", enqueteTeste.getToken())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);

		RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteTeste.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		String json = verificarStatusOk(requestBuilderGet);
		APIResponse<EnqueteDTO> apiResponse = getEnqueteFromResponse(json);

		assertTrue(apiResponse.getData().isIniciada());

	}

	@Test
	public void votarEnqueteSucesso() throws Exception {
		EnqueteDTO enqueteTeste = criarEnquete(5L, TESTE_TOKEN);
		//recuperar enquete e verificar qtd de votos numa determinada opcao
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteTeste.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		String json = verificarStatusOk(requestBuilder);
		APIResponse<EnqueteDTO> apiResponse = getEnqueteFromResponse(json);
		Long qtdVotosOpcaoAntes = apiResponse.getData().getOpcoes().get(0).getQtd();
		long totalVotosAntes = apiResponse.getData().getOpcoes().stream().mapToLong(op -> op.getQtd()).sum();
		
		//votar na opcao verificada enteriormente
		requestBuilder = MockMvcRequestBuilders.post(BASE_ENDPOINT + enqueteTeste.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.content(enqueteTeste.getOpcoes().get(0).toJson())
				.contentType(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);

		//buscar a enquete e verificar se o voto foi computado
		requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteTeste.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		json = verificarStatusOk(requestBuilder);
		apiResponse = getEnqueteFromResponse(json);
		long totalVotosDepois = apiResponse.getData().getOpcoes().stream().mapToLong(op -> op.getQtd()).sum();
		
		assertEquals(qtdVotosOpcaoAntes + 1, apiResponse.getData().getOpcoes().get(0).getQtd());
		
		assertEquals(totalVotosAntes + 1, totalVotosDepois);
	}

	@Test
	public void terminarEnqueteSucesso() throws Exception {
		EnqueteDTO enqueteTeste = criarEnquete(4L, TESTE_TOKEN);
		URI uri = new URI(BASE_ENDPOINT + enqueteTeste.getId() + "/terminar");

		RequestBuilder requestBuilder = MockMvcRequestBuilders.patch(uri).header("token", enqueteTeste.getToken())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);

		RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteTeste.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		String json = verificarStatusOk(requestBuilderGet);
		APIResponse<EnqueteDTO> apiResponse = getEnqueteFromResponse(json);

		assertTrue(apiResponse.getData().isEncerrada());
	}

	@Test
	public void deletarEnqueteSucesso() throws Exception {
		EnqueteDTO enqueteTeste = criarEnquete(6L, TESTE_TOKEN);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(BASE_ENDPOINT + enqueteTeste.getId())
				.header("token", enqueteTeste.getToken()).accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);

		requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteTeste.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
	}

	private String verificarStatusOk(RequestBuilder requestBuilder) throws Exception {
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		return result.getResponse().getContentAsString();
	}

	private APIResponse<List<EnqueteDTO>> getEnquetesFromResponse(String json)
			throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, new TypeReference<APIResponse<List<EnqueteDTO>>>() {
		});
	}

	private APIResponse<EnqueteDTO> getEnqueteFromResponse(String json)
			throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<APIResponse<EnqueteDTO>> tipo = new TypeReference<APIResponse<EnqueteDTO>>() {};
		return mapper.readValue(json, tipo);
	}

	private EnqueteDTO criarEnquete(Long id, String token) {
		EnqueteDTO enqueteTeste = new EnqueteDTO();
		enqueteTeste.setId(id);
		enqueteTeste.setToken(token);
		enqueteTeste.setPergunta("pergunta" + id);

		OpcaoDTO opcao = new OpcaoDTO();
		opcao.setId(1L);
		opcao.setDescricao("Talvez");

		OpcaoDTO opcao2 = new OpcaoDTO();
		opcao2.setId(2L);
		opcao2.setDescricao("Não");

		enqueteTeste.getOpcoes().add(opcao);
		enqueteTeste.getOpcoes().add(opcao2);
		return enqueteTeste;
	}
}
