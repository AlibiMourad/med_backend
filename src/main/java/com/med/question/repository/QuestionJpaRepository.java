package com.med.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.med.question.dto.QuestionDTO;

public interface QuestionJpaRepository extends JpaRepository<QuestionDTO, Long> {
	QuestionDTO findByTitre(String titre);
}
