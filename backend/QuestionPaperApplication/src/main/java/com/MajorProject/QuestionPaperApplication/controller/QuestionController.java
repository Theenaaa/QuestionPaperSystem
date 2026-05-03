package com.MajorProject.QuestionPaperApplication.controller;

import com.MajorProject.QuestionPaperApplication.dto.QuestionPaperResponse;
import com.MajorProject.QuestionPaperApplication.model.Question;
import com.MajorProject.QuestionPaperApplication.service.QuestionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/questions")
@CrossOrigin(origins = "http://localhost:4200")
public class QuestionController {

    @Autowired
    private QuestionService service;

    // ✅ ADD QUESTION
    @PostMapping
    public ResponseEntity<?> addQuestion(@RequestBody Question question) {

        if (question.getMarks() == null || question.getMarks() <= 0) {
            return ResponseEntity.badRequest().body("Marks must be greater than 0");
        }

        if (question.getTopic() == null || question.getTopic().isEmpty()) {
            return ResponseEntity.badRequest().body("Topic is required");
        }

        return ResponseEntity.ok(service.addQuestion(question));
    }

    // ✅ GET ALL QUESTIONS
    @GetMapping
    public ResponseEntity<List<Question>> getAll() {
        return ResponseEntity.ok(service.getAllQuestions());
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteQuestion(@PathVariable int id) {
        service.deleteQuestion(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable int id,
            @RequestBody Question question) {

        if (question.getMarks() == null || question.getMarks() <= 0) {
            return ResponseEntity.badRequest().body("Marks must be greater than 0");
        }

        if (question.getTopic() == null || question.getTopic().isEmpty()) {
            return ResponseEntity.badRequest().body("Topic is required");
        }

        return ResponseEntity.ok(service.updateQuestion(id, question));
    }

    // ✅ SMART PAPER
    @GetMapping("/smart")
    public ResponseEntity<QuestionPaperResponse> generateSmartPaper(@RequestParam String subject) {
        return ResponseEntity.ok(service.generateSmartPaper(subject));
    }

    // ✅ PDF EXPORT (AUTO)
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportPdf(@RequestParam String subject) {

        byte[] pdfData = service.generatePdf(subject);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=question_paper.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfData);
    }

    // ✅ SEARCH
    @GetMapping("/search")
    public ResponseEntity<List<Question>> searchQuestions(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String difficulty) {

        return ResponseEntity.ok(service.searchQuestions(subject, difficulty));
    }

    // ✅ PAGINATION
    @GetMapping("/page")
    public ResponseEntity<Page<Question>> getPaginated(
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity.ok(service.getPaginatedQuestions(page, size));
    }

    // ✅ FLEXIBLE GENERATION
    @GetMapping("/generate-flexible")
    public ResponseEntity<List<Question>> generateFlexible(
            @RequestParam String subject,
            @RequestParam int totalQuestions,
            @RequestParam(required = false) Integer easy,
            @RequestParam(required = false) Integer medium,
            @RequestParam(required = false) Integer hard
    ) {
        return ResponseEntity.ok(
                service.generateFlexiblePaper(subject, totalQuestions, easy, medium, hard)
        );
    }

    // ✅ MARKS-BASED GENERATION
    @GetMapping("/generate-by-marks")
    public ResponseEntity<List<Question>> generateByMarks(
            @RequestParam String subject,
            @RequestParam int totalMarks
    ) {
        return ResponseEntity.ok(service.generateByMarks(subject, totalMarks));
    }

    // ✅ GENERATE FROM SELECTED QUESTIONS
    @PostMapping("/generate-selected")
    public ResponseEntity<List<Question>> generateSelected(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(service.generateFromSelected(ids));
    }

    // 🔥 EXPORT SELECTED QUESTIONS AS PDF
    @PostMapping("/export-selected")
    public ResponseEntity<byte[]> exportSelectedPdf(@RequestBody List<Integer> ids) {

        List<Question> selected = service.generateFromSelected(ids);

        // Optional: randomize order
        Collections.shuffle(selected);

        byte[] pdfBytes = service.generatePdfFromQuestions(selected);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Custom_Paper.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}