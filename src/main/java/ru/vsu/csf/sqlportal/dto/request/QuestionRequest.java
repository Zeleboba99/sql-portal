package ru.vsu.csf.sqlportal.dto.request;

import lombok.Getter;

@Getter
public class QuestionRequest {
    private Long id;
    private String text;
    private AnswerRequest answer;
}
