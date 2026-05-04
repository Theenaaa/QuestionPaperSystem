package com.MajorProject.QuestionPaperApplication.service;

import com.MajorProject.QuestionPaperApplication.dto.QuestionPaperResponse;
import com.MajorProject.QuestionPaperApplication.model.Question;
import com.MajorProject.QuestionPaperApplication.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository repo;

    // ✅ ADD QUESTION
    public Question addQuestion(Question question) {

        boolean exists = repo.existsByTextAndSubjectAndDifficulty(
                question.getText(),
                question.getSubject(),
                question.getDifficulty()
        );

        if (exists) {
            throw new RuntimeException("Question already exists!");
        }

        return repo.save(question);
    }

    // ✅ GET ALL
    public List<Question> getAllQuestions() {
        return repo.findAll();
    }

    // 🔥 FLEXIBLE GENERATION (MAIN UPGRADE)
    public List<Question> generateFlexiblePaper(
            String subject,
            int totalQuestions,
            Integer easyCount,
            Integer mediumCount,
            Integer hardCount
    ) {

        List<Question> all = repo.findAll()
                .stream()
                .filter(q -> q.getSubject().equalsIgnoreCase(subject))
                .toList();

        List<Question> easy = new ArrayList<>();
        List<Question> medium = new ArrayList<>();
        List<Question> hard = new ArrayList<>();

        for (Question q : all) {
            switch (q.getDifficulty().toLowerCase()) {
                case "easy" -> easy.add(q);
                case "medium" -> medium.add(q);
                case "hard" -> hard.add(q);
            }
        }

        Collections.shuffle(easy);
        Collections.shuffle(medium);
        Collections.shuffle(hard);

        List<Question> result = new ArrayList<>();

        // Default distribution
        if (easyCount == null || mediumCount == null || hardCount == null) {
            easyCount = totalQuestions / 3;
            mediumCount = totalQuestions / 3;
            hardCount = totalQuestions - (easyCount + mediumCount);
        }

        result.addAll(easy.subList(0, Math.min(easyCount, easy.size())));
        result.addAll(medium.subList(0, Math.min(mediumCount, medium.size())));
        result.addAll(hard.subList(0, Math.min(hardCount, hard.size())));

        // Fill remaining randomly
        if (result.size() < totalQuestions) {

            List<Question> remaining = new ArrayList<>(all);
            remaining.removeAll(result);

            Collections.shuffle(remaining);

            for (Question q : remaining) {
                if (result.size() >= totalQuestions) break;
                result.add(q);
            }
        }

        return result;
    }

    // 🔥 MARKS-BASED GENERATION
    public List<Question> generateByMarks(String subject, int totalMarks) {

        List<Question> all = repo.findAll()
                .stream()
                .filter(q -> q.getSubject().equalsIgnoreCase(subject))
                .toList();

        Collections.shuffle(all);

        List<Question> result = new ArrayList<>();
        int sum = 0;

        for (Question q : all) {
            if (sum + q.getMarks() <= totalMarks) {
                result.add(q);
                sum += q.getMarks();
            }
        }

        return result;
    }

    // ✅ SMART PAPER (keep existing)
    public QuestionPaperResponse generateSmartPaper(String subject) {

        List<Question> easy = repo.findBySubjectIgnoreCaseAndDifficultyIgnoreCase(subject, "easy");
        List<Question> medium = repo.findBySubjectIgnoreCaseAndDifficultyIgnoreCase(subject, "medium");
        List<Question> hard = repo.findBySubjectIgnoreCaseAndDifficultyIgnoreCase(subject, "hard");

        Collections.shuffle(easy);
        Collections.shuffle(medium);
        Collections.shuffle(hard);

        QuestionPaperResponse paper = new QuestionPaperResponse();

        paper.setEasyQuestions(easy.subList(0, Math.min(2, easy.size())));
        paper.setMediumQuestions(medium.subList(0, Math.min(2, medium.size())));
        paper.setHardQuestions(hard.subList(0, Math.min(1, hard.size())));

        return paper;
    }

    // ✅ PDF
    public byte[] generatePdf(String subject) {

        List<Question> questions = generateFlexiblePaper(subject, 5, null, null, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("QUESTION PAPER - " + subject).setBold().setFontSize(18));

        int i = 1;
        for (Question q : questions) {
            document.add(new Paragraph(i++ + ". " + q.getText() + " (" + q.getMarks() + " Marks)"));
        }

        document.close();

        return out.toByteArray();
    }

    // CRUD
    public void deleteQuestion(int id) {
        repo.deleteById(id);
    }

    public Question updateQuestion(int id, Question updatedQuestion) {
        Question existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));

        existing.setText(updatedQuestion.getText());
        existing.setSubject(updatedQuestion.getSubject());
        existing.setDifficulty(updatedQuestion.getDifficulty());
        existing.setTopic(updatedQuestion.getTopic());
        existing.setMarks(updatedQuestion.getMarks());

        return repo.save(existing);
    }

    public List<Question> searchQuestions(String subject, String difficulty) {

        if (subject != null && difficulty != null)
            return repo.findBySubjectIgnoreCaseAndDifficultyIgnoreCase(subject, difficulty);

        return repo.findAll();
    }

    public Page<Question> getPaginatedQuestions(int page, int size) {
        return repo.findAll(PageRequest.of(page, size));
    }
    public List<Question> generateFromSelected(List<Integer> ids) {

        List<Question> selected = repo.findAllById(ids);

        // Optional: shuffle to randomize order
        Collections.shuffle(selected);

        return selected;
    }
    public byte[] generatePdfFromQuestions(List<Question> questions) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("CUSTOM QUESTION PAPER")
                .setBold().setFontSize(18));

        document.add(new Paragraph("\n"));

        int i = 1;
        for (Question q : questions) {
            document.add(new Paragraph(
                    i++ + ". " + q.getText() +
                            " (" + q.getMarks() + " Marks - " + q.getDifficulty() + ")"
            ));
        }

        document.close();

        return out.toByteArray();
    }
}