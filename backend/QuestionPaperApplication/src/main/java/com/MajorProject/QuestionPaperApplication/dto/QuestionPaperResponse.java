package com.MajorProject.QuestionPaperApplication.dto;


import com.MajorProject.QuestionPaperApplication.model.Question;
import java.util.List;

public class QuestionPaperResponse {

    private List<Question> easyQuestions;
    private List<Question> mediumQuestions;
    private List<Question> hardQuestions;

    public List<Question> getEasyQuestions() {
        return easyQuestions;
    }

    public void setEasyQuestions(List<Question> easyQuestions) {
        this.easyQuestions = easyQuestions;
    }

    public List<Question> getMediumQuestions() {
        return mediumQuestions;
    }

    public void setMediumQuestions(List<Question> mediumQuestions) {
        this.mediumQuestions = mediumQuestions;
    }

    public List<Question> getHardQuestions() {
        return hardQuestions;
    }

    public void setHardQuestions(List<Question> hardQuestions) {
        this.hardQuestions = hardQuestions;
    }
}
