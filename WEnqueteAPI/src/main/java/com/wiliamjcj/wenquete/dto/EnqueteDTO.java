package com.wiliamjcj.wenquete.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.wiliamjcj.wenquete.utils.Jsonable;

public class EnqueteDTO implements Jsonable {

	private Long id;
	private String token;
	
	@NotEmpty(message="{enquetedto.pergunta.notnull.msg}")
	private String pergunta;
	
	@Size(min=2, message="{enquetedto.opcoes.size.msg}")
	private List<OpcaoDTO> opcoes;
	
	private boolean iniciada;
	private boolean encerrada;

	public boolean isIniciada() {
		return iniciada;
	}

	public void setIniciada(boolean iniciada) {
		this.iniciada = iniciada;
	}

	public boolean isEncerrada() {
		return encerrada;
	}

	public void setEncerrada(boolean encerrada) {
		this.encerrada = encerrada;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public List<OpcaoDTO> getOpcoes() {
		if(null == opcoes)
			opcoes = new ArrayList<OpcaoDTO>();
		
		return opcoes;
	}

	public void setOpcoes(List<OpcaoDTO> opcoes) {
		this.opcoes = opcoes;
	}

}
