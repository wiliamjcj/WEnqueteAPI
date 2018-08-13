package com.wiliamjcj.wenquete.utils;

import java.util.ArrayList;
import java.util.List;

public class APIResponse<T> {

	private T data;
	private List<String> errors;

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
