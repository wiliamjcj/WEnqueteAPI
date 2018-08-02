package com.wiliamjcj.wenquete.dto;

import com.wiliamjcj.wenquete.utils.Jsonable;

public class OpcaoDTO implements Jsonable {

	private Long id;
	private long qtd;
	private String descricao;
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public long getQtd() {
		return qtd;
	}

	public void setQtd(long qtd) {
		this.qtd = qtd;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	
}
