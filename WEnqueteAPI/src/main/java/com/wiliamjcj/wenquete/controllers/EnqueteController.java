package com.wiliamjcj.wenquete.controllers;

import java.net.URI;
import java.net.URISyntaxException;

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

@RestController
@RequestMapping(value = "/api/enquetes")
public class EnqueteController {

	@GetMapping
	public ResponseEntity<String> listar() {
		return ResponseEntity.ok("");
	}

	@PostMapping
	public ResponseEntity<String> adicionarEnquete(@RequestBody String enquete) throws URISyntaxException {
		return ResponseEntity.created(new URI("")).build();
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<String> buscarEnquete(@PathVariable("id") Long id) {
		return ResponseEntity.ok("");
	}

	@PatchMapping(value = "/{id}/iniciar")
	public ResponseEntity<String> iniciarEnquete(@PathVariable("id") Long id,
			@RequestHeader(value = "token") String token) {
		return ResponseEntity.ok("");
	}

	@PostMapping(value = "/{id}")
	public ResponseEntity<String> votarEnquete(@PathVariable("id") Long id, @RequestBody String opcao) {
		return ResponseEntity.ok("");
	}

	@PatchMapping(value = "/{id}/terminar")
	public ResponseEntity<String> terminarEnquete(@PathVariable("id") Long id,
			@RequestHeader(value = "token") String token) {
		return ResponseEntity.ok("");
	}

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<String> deletarEnquete(@PathVariable("id") Long id) {
		return ResponseEntity.ok("");
	}
}
