package com.studybuddy.backend.controller;

import com.studybuddy.backend.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini-test")
public class GeminiTestController {

    @Autowired
    private GeminiService geminiService;

    @GetMapping
    public String test(@RequestParam String text) {
        return geminiService.generate(null, text);
    }
}