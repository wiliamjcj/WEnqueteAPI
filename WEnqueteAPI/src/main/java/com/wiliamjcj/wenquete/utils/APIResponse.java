package com.wiliamjcj.wenquete.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class APIResponse<T> {

	private T data;
	private List<String> errors;
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<String> getErrors() {
		if (null == errors)
			errors = new ArrayList<String>();

		return errors;
	}

}
