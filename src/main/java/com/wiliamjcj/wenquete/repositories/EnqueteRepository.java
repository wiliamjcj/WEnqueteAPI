package com.wiliamjcj.wenquete.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.wiliamjcj.wenquete.entities.Enquete;

public interface EnqueteRepository extends JpaRepository<Enquete, Long>{

	Page<Enquete> findByIniciada(boolean b, Pageable pageable);

}
