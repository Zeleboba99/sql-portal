package ru.vsu.csf.sqlportal.dto.request;

import lombok.Getter;

@Getter
public class QuestionRequest {
    private String text;
    private String rightAnswer;
}
