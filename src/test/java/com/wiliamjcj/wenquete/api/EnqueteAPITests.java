package com.wiliamjcj.wenquete.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
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
import com.wiliamjcj.wenquete.WEnqueteApiApplication;
import com.wiliamjcj.wenquete.dto.EnqueteDTO;
import com.wiliamjcj.wenquete.dto.OpcaoDTO;
import com.wiliamjcj.wenquete.utils.APIResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = WEnqueteApiApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
  locations = "classpath:application-dev.properties")
public class EnqueteAPITests {

	@Autowired
	private MockMvc mockMvc;
	
	@Value("${rest.enquete.mapping}")
	private String BASE_ENDPOINT;
	
	@Value("${enquete.fake.token}")
	private String TESTE_TOKEN ;
	
	private EnqueteDTO enquetebuscar;
	private EnqueteDTO enqueteIniciar;
	private EnqueteDTO enqueteTerminar;
	private EnqueteDTO enqueteVotar;
	private EnqueteDTO enqueteDeletar;
	

	@Before
	public void setup() {
		BASE_ENDPOINT += "/";
		
		enquetebuscar = criarEnqueteTeste(1l,1l,2l);
		
		enqueteIniciar = criarEnqueteTeste(2l,3l,4l);
		enqueteIniciar.setToken(TESTE_TOKEN);
		
		enqueteTerminar = criarEnqueteTeste(3l,5l,6l);
		enqueteTerminar.setToken(TESTE_TOKEN);
		enqueteTerminar.setIniciada(true);
		
		enqueteVotar = criarEnqueteTeste(4l,7l,8l);
		enqueteVotar.setIniciada(true);
		
		enqueteDeletar = criarEnqueteTeste(5l,9l,10l);
		enqueteDeletar.setToken(TESTE_TOKEN);
	}

	private EnqueteDTO criarEnqueteTeste(Long idEnquete, Long idOpcao1, Long idOpcao2) {
		EnqueteDTO enqueteTeste = new EnqueteDTO();
		enqueteTeste.setId(idEnquete);
		enqueteTeste.setPergunta("Qual a sua cor preferida?");

		OpcaoDTO opcao = new OpcaoDTO();
		opcao.setId(idOpcao1);
		opcao.setDescricao("Azul");

		OpcaoDTO opcao2 = new OpcaoDTO();
		opcao2.setId(idOpcao2);
		opcao2.setDescricao("Vermelho");

		enqueteTeste.getOpcoes().add(opcao);
		enqueteTeste.getOpcoes().add(opcao2);
		return enqueteTeste;
	}
	
	@Test
	public void adicionarEnqueteSucesso() throws Exception {
		
		EnqueteDTO enqueteTeste = criarEnquete(null, null);

		String json = enqueteTeste.toJson();
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post(BASE_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.content(json)
				.contentType(MediaType.APPLICATION_JSON_UTF8);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		int status = result.getResponse().getStatus();
		assertEquals(HttpStatus.CREATED.value(), status);

		String location = result.getResponse().getHeader("location");
		assertThat(location).isNotBlank();

		String token = result.getResponse().getHeader("token");
		assertThat(token).isNotBlank();
	}
	@Test
	public void listarTodasEnquetesSucesso() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT)
				.accept(MediaType.APPLICATION_JSON_UTF8);
		
		String json = verificarStatusOk(requestBuilder);
		
		List<EnqueteDTO> enquetes = getEnquetesFromResponse(json); 

		assertThat(enquetes).isNotNull();

		assertTrue(enquetes.stream()
			.allMatch(enquete -> !StringUtils.isEmpty(enquete.getPergunta()) 
					&& !enquete.getOpcoes().isEmpty()
					&& enquete.getOpcoes().stream()
						.allMatch(opcao -> null != opcao && null != opcao.getId())
			)
		);
	}

	@Test
	public void buscarEnqueteSucesso() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + enquetebuscar.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		String json = verificarStatusOk(requestBuilder);
		APIResponse<EnqueteDTO> apiResponse = getEnqueteFromResponse(json);

		assertEquals(enquetebuscar.toJson(), apiResponse.getData().toJson());
	}

	@Test
	public void iniciarEnqueteSucesso() throws Exception {
		URI uri = new URI(BASE_ENDPOINT + enqueteIniciar.getId() + "/iniciar");

		RequestBuilder requestBuilder = MockMvcRequestBuilders.patch(uri)
				.header("token", enqueteIniciar.getToken())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);

		RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteIniciar.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		String json = verificarStatusOk(requestBuilderGet);
		APIResponse<EnqueteDTO> apiResponse = getEnqueteFromResponse(json);

		assertTrue(apiResponse.getData().isIniciada());

	}

	@Test
	public void votarEnqueteSucesso() throws Exception {
		//recuperar enquete e verificar qtd de votos numa determinada opcao
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteVotar.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		String json = verificarStatusOk(requestBuilder);
		APIResponse<EnqueteDTO> apiResponse = getEnqueteFromResponse(json);
		Long qtdVotosOpcaoAntes = apiResponse.getData().getOpcoes().get(0).getQuantidade();
		long totalVotosAntes = apiResponse.getData().getOpcoes().stream().mapToLong(op -> op.getQuantidade()).sum();
		
		//votar na opcao verificada enteriormente
		requestBuilder = MockMvcRequestBuilders.post(BASE_ENDPOINT + enqueteVotar.getId()+"/"+enqueteVotar.getOpcoes().get(0).getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		//verificar se o voto foi computado
		json = verificarStatusOk(requestBuilder);
		apiResponse = getEnqueteFromResponse(json);
		long totalVotosDepois = apiResponse.getData().getOpcoes().stream().mapToLong(op -> op.getQuantidade()).sum();

		assertEquals(qtdVotosOpcaoAntes + 1, apiResponse.getData().getOpcoes().get(0).getQuantidade());
		
		assertEquals(totalVotosAntes + 1, totalVotosDepois);
	}

	@Test
	public void terminarEnqueteSucesso() throws Exception {
		URI uri = new URI(BASE_ENDPOINT + enqueteTerminar.getId() + "/terminar");

		RequestBuilder requestBuilder = MockMvcRequestBuilders.patch(uri).header("token", enqueteTerminar.getToken())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);

		RequestBuilder requestBuilderGet = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteTerminar.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		String json = verificarStatusOk(requestBuilderGet);
		APIResponse<EnqueteDTO> apiResponse = getEnqueteFromResponse(json);

		assertTrue(apiResponse.getData().isEncerrada());
	}

	@Test
	public void deletarEnqueteSucesso() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(BASE_ENDPOINT + enqueteDeletar.getId())
				.header("token", enqueteDeletar.getToken()).accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);

		requestBuilder = MockMvcRequestBuilders.get(BASE_ENDPOINT + enqueteDeletar.getId())
				.accept(MediaType.APPLICATION_JSON_UTF8);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
	}

	private String verificarStatusOk(RequestBuilder requestBuilder) throws Exception {
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
		return result.getResponse().getContentAsString();
	}

	private List<EnqueteDTO> getEnquetesFromResponse(String json)
			throws IOException, JsonParseException, JsonMappingException, JSONException {
		
		ObjectMapper mapper = new ObjectMapper();
		//return mapper.readValue(json, new TypeReference<APIResponse<ResponsePage<EnqueteDTO>>>(){});
		
		JSONObject jo = new JSONObject(json);
		String listJson = jo.getJSONObject("data").getString("content");
		
		return mapper.readValue(listJson,new TypeReference<List<EnqueteDTO>>(){});
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
		enqueteTeste.setPergunta("pergunta " + id);

		OpcaoDTO opcao = new OpcaoDTO();
		opcao.setDescricao("Talvez");

		OpcaoDTO opcao2 = new OpcaoDTO();
		opcao2.setDescricao("Não");

		enqueteTeste.getOpcoes().add(opcao);
		enqueteTeste.getOpcoes().add(opcao2);
		return enqueteTeste;
	}
	
}
