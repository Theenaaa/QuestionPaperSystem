package com.MajorProject.QuestionPaperApplication.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/")
    public String home() {
        return "Question Paper Application is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}