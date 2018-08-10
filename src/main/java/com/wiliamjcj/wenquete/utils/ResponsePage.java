package com.wiliamjcj.wenquete.utils;

import java.util.List;

import org.springframework.data.domain.PageImpl;

public class ResponsePage<T> extends PageImpl<T>{

  public ResponsePage(List<T> content) {
		super(content);
		// TODO Auto-generated constructor stub
	}

private static final long serialVersionUID = 3248189030448292002L;

 

} 