package com.med.question.rest;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.aspectj.weaver.patterns.TypePatternQuestions.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.med.question.dto.QuestionDTO;
import com.med.question.repository.QuestionJpaRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/question")
public class QuestionController {

	public static final Logger logger = LoggerFactory.getLogger(Question.class);

	private QuestionJpaRepository questionsJpaRepository;

	@Autowired
	public void setQuestionsRepo(QuestionJpaRepository questionsJpaRepository) {
		this.questionsJpaRepository = questionsJpaRepository;
	}

	// Get All QUESTIONS : //
	@GetMapping("/")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<List<QuestionDTO>> listAllQuestions() {
		List<QuestionDTO> questions = questionsJpaRepository.findAll();
		if (questions.isEmpty()) {
			return new ResponseEntity<List<QuestionDTO>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<QuestionDTO>>(questions, HttpStatus.OK);
	}

	// Add new QUESTIONS : //
	@PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<QuestionDTO> createQuestions(@Valid @RequestBody final QuestionDTO questions) {
		logger.info("Creating Questions : {}", questions);
		if (questionsJpaRepository.findByTitre(questions.getTitre()) != null) {
			logger.error("Unable to create. A Questions with ncin {} already exist", questions.getTitre());
			return new ResponseEntity<QuestionDTO>(new QuestionDTO(), HttpStatus.CONFLICT);
		}
		questionsJpaRepository.save(questions);
		return new ResponseEntity<QuestionDTO>(questions, HttpStatus.CONFLICT);
	}

	// Get QUESTIONS by ID : //
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<QuestionDTO> getQuestionsById(@PathVariable("id") final Long id) {
		Optional<QuestionDTO> questions = questionsJpaRepository.findById(id);
		if (!questions.isPresent()) {
			return new ResponseEntity<QuestionDTO>(new QuestionDTO(), HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<QuestionDTO>(questions.get(), HttpStatus.OK);
	}

	// Update QUESTIONS by ID : //
	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<QuestionDTO> updateQuestions(@PathVariable("id") final Long id,
			@RequestBody QuestionDTO questions) {
		Optional<QuestionDTO> currentQuestions = questionsJpaRepository.findById(id);
		if (!currentQuestions.isPresent()) {
			return new ResponseEntity<QuestionDTO>(new QuestionDTO(), HttpStatus.NOT_FOUND);
		}
		currentQuestions.get().setTitre(questions.getTitre());
		currentQuestions.get().setContenu(questions.getContenu());
		currentQuestions.get().setFileName(questions.getFileName());
		currentQuestions.get().setFileName(questions.getFileName());
		currentQuestions.get().setData(questions.getData());
		currentQuestions.get().setCategorie(questions.getCategorie());
		currentQuestions.get().setSpecialite(questions.getSpecialite());

		questionsJpaRepository.saveAndFlush(currentQuestions.get());
		return new ResponseEntity<QuestionDTO>(currentQuestions.get(), HttpStatus.OK);
	}

	// Delete QUESTIONS by ID : //
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<QuestionDTO> deleteQuestions(@PathVariable("id") final Long id) {
		Optional<QuestionDTO> questions = questionsJpaRepository.findById(id);
		if (!questions.isPresent()) {
			return new ResponseEntity<QuestionDTO>(new QuestionDTO(), HttpStatus.NOT_FOUND);
		}
		questionsJpaRepository.deleteById(id);
		return new ResponseEntity<QuestionDTO>(HttpStatus.NO_CONTENT);
	}
}
