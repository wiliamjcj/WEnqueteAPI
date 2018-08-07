package com.wiliamjcj.wenquete.messages;

import org.springframework.http.HttpStatus;

public enum ResponseMsg implements MessageProvider{
	
	ENQUETE_CNTRL_INICIAR_SUCESSO("enquetecontroller.iniciarenquete.sucesso", HttpStatus.OK),
	ENQUETE_CNTRL_TERMINAR_SUCESSO("enquetecontroller.terminarenquete.sucesso", HttpStatus.OK),
	ENQUETE_CNTRL_ENQUETE_NOCONTENT("enquetecontroller.enquete.nocontent", HttpStatus.NO_CONTENT ),
	ENQUETE_CNTRL_OPCAO_NOCONTENT("enquetecontroller.opcao.nocontent", HttpStatus.NO_CONTENT),
	ENQUETE_CNTRL_ENCERRADA("enquetecontroller.encerrada", HttpStatus.CONFLICT),
	ENQUETE_CNTRL_TOKEN_INVALIDO("enquetecontroller.token.invalido", HttpStatus.UNAUTHORIZED)
	;
	
	private String key;
	private HttpStatus status;
	
	private ResponseMsg(String key, HttpStatus status) {
		this.key = key;
		this.status = status;
	}

	public String getKey() {
		return key;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
