package com.wiliamjcj.wenquete.dto;

import java.lang.reflect.Type;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Scope(value=ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class DTOMapper {

	private ModelMapper modelMapper;
	
	public DTOMapper() {
		modelMapper = new ModelMapper();
	}
	
	public Object map(Object obj, Type tipo) {
		return modelMapper.map(obj, tipo);
	}
	
	public <S,D> Page<D> mapPage(Page<S> page, Class<D> tipoClasse) {
		Page<Object> pageObj = page.map(e -> modelMapper.map(e, tipoClasse));
		Type tipo = new TypeToken<Page<D>>() {}.getType();
		return modelMapper.map(pageObj, tipo);
	}

	public ModelMapper getModelMapper() {
		return modelMapper;
	}

	public void setModelMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}
	
	
}

