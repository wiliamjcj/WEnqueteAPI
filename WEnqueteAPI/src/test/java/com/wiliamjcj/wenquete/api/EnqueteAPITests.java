package com.wiliamjcj.wenquete.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.wiliamjcj.wenquete.controllers.EnqueteController;

@RunWith(SpringRunner.class)
@WebMvcTest(value = EnqueteController.class, secure = false)
public class EnqueteAPITests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void adicionarEnquete() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/enquetes")
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.content("{}").contentType(MediaType.APPLICATION_JSON_UTF8);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
	}

	@Test
	public void listarTodasEnquetes() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/enquetes")
				.accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);
	}

	@Test
	public void buscarEnquete() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/enquetes/1")
				.accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);
	}

	@Test
	public void iniciarEnquete() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/enquetes/1/iniciar")
				.header("token", "123")
				.accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);
	}

	@Test
	public void votarEnquete() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/enquetes/1")
				.accept(MediaType.APPLICATION_JSON_UTF8)
				.content("{}").contentType(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);
	}

	@Test
	public void terminarEnquete() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/api/enquetes/1/terminar")
				.header("token", "123")
				.accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);
	}

	@Test
	public void deletarEnquete() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/enquetes/1")
				.header("token", "123")
				.accept(MediaType.APPLICATION_JSON_UTF8);

		verificarStatusOk(requestBuilder);
	}

	private void verificarStatusOk(RequestBuilder requestBuilder) throws Exception {
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
	}
}
