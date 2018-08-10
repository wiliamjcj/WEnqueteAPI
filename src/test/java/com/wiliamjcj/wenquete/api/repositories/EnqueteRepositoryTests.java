package com.wiliamjcj.wenquete.api.repositories;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.wiliamjcj.wenquete.entities.Enquete;
import com.wiliamjcj.wenquete.repositories.EnqueteRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class EnqueteRepositoryTests {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private EnqueteRepository enqueteRepository;

	@Test
	public void findAll() {
		List<Enquete> all = enqueteRepository.findAll();
	}

	@Test
	public void findById() {

	}

	@Test
	public void create() {

	}

	@Test
	public void update() {

	}

	@Test
	public void delete() {

	}
}
