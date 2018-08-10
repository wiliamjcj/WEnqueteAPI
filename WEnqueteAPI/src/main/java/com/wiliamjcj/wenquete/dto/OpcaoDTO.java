package com.wiliamjcj.wenquete.dto;

import javax.validation.constraints.NotEmpty;

import com.wiliamjcj.wenquete.utils.Jsonable;

public class OpcaoDTO implements Jsonable
{

    private Long id;
    private long quantidade;

    @NotEmpty(message = "{opcaodto.descricao.notnull.msg}")
    private String descricao;

    public void votar()
    {
        this.quantidade += 1;
    }

    public String getDescricao()
    {
        return descricao;
    }

    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }

    public long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(long quantidade) {
		this.quantidade = quantidade;
	}

	public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

}
