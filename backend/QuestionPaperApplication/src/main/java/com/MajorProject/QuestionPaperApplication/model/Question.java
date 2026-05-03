package com.MajorProject.QuestionPaperApplication.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "question",
        uniqueConstraints = @UniqueConstraint(columnNames = {"text", "subject", "difficulty"})
)
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String text;
    private String subject;
    private String difficulty;

    // ✅ NEW FIELDS
    private String topic;
    private Integer marks;

    public Question() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public Integer getMarks() { return marks; }
    public void setMarks(int marks) { this.marks = marks; }
}