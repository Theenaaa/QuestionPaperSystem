package com.MajorProject.QuestionPaperApplication.repository;

import com.MajorProject.QuestionPaperApplication.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface QuestionRepository extends JpaRepository<Question, Integer> {

    // ✅ CASE-INSENSITIVE SEARCH (VERY IMPORTANT)
    List<Question> findBySubjectIgnoreCaseAndDifficultyIgnoreCase(String subject, String difficulty);

    boolean existsByTextAndSubjectAndDifficulty(String text, String subject, String difficulty);
    Page<Question> findAll(Pageable pageable);

}