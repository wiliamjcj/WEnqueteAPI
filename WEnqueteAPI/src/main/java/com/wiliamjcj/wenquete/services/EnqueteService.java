package com.wiliamjcj.wenquete.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wiliamjcj.wenquete.dto.DTOMapper;
import com.wiliamjcj.wenquete.dto.EnqueteDTO;
import com.wiliamjcj.wenquete.entities.Enquete;
import com.wiliamjcj.wenquete.repositories.EnqueteRepository;

@Service
public class EnqueteService {

	@Autowired
	EnqueteRepository enqueteRepository;

	@Autowired
	DTOMapper mapper;

	public Page<EnqueteDTO> buscarEnquetes(Pageable p) {
		Page<Enquete> enquetes = enqueteRepository.findAll(p);
		
		Page<EnqueteDTO> enqDto = mapper.mapPage(enquetes, EnqueteDTO.class);
		
		return enqDto;
	}

	public void salvarEnquete(EnqueteDTO dto) {
		Enquete enquete = (Enquete) mapper.map(dto, Enquete.class);
		enqueteRepository.save(enquete);
	}
}
