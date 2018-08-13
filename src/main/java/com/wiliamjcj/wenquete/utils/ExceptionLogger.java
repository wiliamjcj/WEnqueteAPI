package com.wiliamjcj.wenquete.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Component
public class ExceptionLogger implements Jsonable {

	@JsonIgnore
	private transient final Logger log = LoggerFactory.getLogger(this.getClass());

	@JsonIgnore
	@Value("${logging.printstatcktrace}")
	private transient String printStackTrace;
	
	@JsonIgnore
	@Value("${logging.printfullstatcktrace}")
	private transient String printFullStackTrace;

	private List<StackTraceElement> stackTrace;
	private String mensagem;
	private String tipoExcecao;

	public void erro(Exception e) {
		try {
			List<StackTraceElement> stackTraceAsList = Arrays.asList(e.getStackTrace());
			
			if(Boolean.parseBoolean(printFullStackTrace)) {
				e.printStackTrace();
			}else if (Boolean.parseBoolean(printStackTrace)) {
				e.setStackTrace(stackTraceAsList.stream()
						.filter(st -> st.getClassName().contains("com.wiliamjcj.wenquete"))
						.toArray(StackTraceElement[]::new)
				);
				e.printStackTrace();
			}

			tipoExcecao = e.getClass().getName();
			mensagem = e.getMessage();
			stackTrace = stackTraceAsList.stream().filter(st -> st.getClassName().contains("com.wiliamjcj.wenquete"))
					.map(el -> new StackTraceElement(el.getClassName(), el.getMethodName(), null, el.getLineNumber()))
					.collect(Collectors.toList());

			log.error(this.toPrettyJson());
		} catch (Exception e2) {
			e2.printStackTrace();
			log.error(e.getMessage());
		}
	}

	public List<StackTraceElement> getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(List<StackTraceElement> stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getTipoExcecao() {
		return tipoExcecao;
	}

	public void setTipoExcecao(String tipoExcecao) {
		this.tipoExcecao = tipoExcecao;
	}
}
