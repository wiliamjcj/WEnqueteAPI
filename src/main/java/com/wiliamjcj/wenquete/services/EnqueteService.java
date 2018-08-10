package com.wiliamjcj.wenquete.services;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

	@Value("${application.secrete.key}")
	private String chaveToken;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Busca todas as enquetes de forma paginada.
	 * 
	 * @param pageable Pageable contendo as informacoes da paginacao
	 * @return Page<EnqueteDTO>
	 */
	public Page<EnqueteDTO> buscarEnquetes(Pageable pageable) {
		Page<Enquete> enquetes = enqueteRepository.findByIniciada(true, pageable);

		Page<EnqueteDTO> enquetesDTO = mapper.mapPage(enquetes, EnqueteDTO.class);

		return enquetesDTO;
	}

	/**
	 * Persiste uma nova enquete no banco, gera um token baseado na pergunta e no id
	 * gerado ao persistir, e atualiza a enquete.
	 * 
	 * @param dto
	 * @return EnqueteDTO enquete persistida.
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 */
	public EnqueteDTO criarEnquete(EnqueteDTO dto) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Enquete enquete = (Enquete) mapper.map(dto, Enquete.class);
		enquete = enqueteRepository.save(enquete);

		enquete.setToken("BA5F0DCF723C4D1567083058D0776CCCECB4B208");//generateToken(enquete));
		enquete = enqueteRepository.save(enquete);

		return (EnqueteDTO) mapper.map(enquete, EnqueteDTO.class);
	}

	public EnqueteDTO atualizarEnquete(EnqueteDTO dto) {
		Enquete enquete = (Enquete) mapper.map(dto, Enquete.class);
		enquete = enqueteRepository.save(enquete);

		return (EnqueteDTO) mapper.map(enquete, EnqueteDTO.class);
	}

	public EnqueteDTO buscarEnquete(Long id) {
		Optional<Enquete> enquete = enqueteRepository.findById(id);
		if (enquete.isPresent())
			return (EnqueteDTO) mapper.map(enquete.get(), EnqueteDTO.class);
		else
			return new EnqueteDTO();
	}

	public void deletarEnquete(EnqueteDTO dto) {
		Enquete enquete = (Enquete) mapper.map(dto, Enquete.class);
		enqueteRepository.delete(enquete);
	}

	private String generateToken(Enquete e) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest msdDigest = MessageDigest.getInstance("SHA-1");
		String str = e.getId() + e.getPergunta() + chaveToken;
		msdDigest.update(str.getBytes("UTF-8"), 0, str.length());
		return DatatypeConverter.printHexBinary(msdDigest.digest());
	}
}
