package com.wiliamjcj.wenquete.dto;

import javax.validation.constraints.NotEmpty;

import com.wiliamjcj.wenquete.utils.Jsonable;

public class OpcaoDTO implements Jsonable
{

    private Long id;
    private long qtd;

    @NotEmpty(message = "{opcaodto.descricao.notnull.msg}")
    private String descricao;

    public void votar()
    {
        this.qtd += 1;
    }

    public String getDescricao()
    {
        return descricao;
    }

    public void setDescricao(String descricao)
    {
        this.descricao = descricao;
    }

    public long getQtd()
    {
        return qtd;
    }

    public void setQtd(long qtd)
    {
        this.qtd = qtd;
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
