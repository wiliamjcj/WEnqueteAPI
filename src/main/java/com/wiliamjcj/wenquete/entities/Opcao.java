package com.wiliamjcj.wenquete.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity(name="opcao")
public class Opcao {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator="OPCAO_GEN")
	@SequenceGenerator(name="OPCAO_GEN", sequenceName="OPCAO_SEQ", allocationSize=1)
	private Long id;
	
	private long quantidade;
	
	private String descricao;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_criacao")
	private Date dataCriacao;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_atualizacao")
	private Date dataAtualizacao;

	@PrePersist
	private void prePersist() {
		Date d = new Date();
		dataCriacao = d;
		dataAtualizacao = d;
	}

	@PreUpdate
	private void preUpdate() {
		dataAtualizacao = new Date();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(long qtd) {
		this.quantidade = qtd;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
