package ru.vsu.csf.sqlportal.dto.request;

import lombok.Getter;

@Getter
public class AnswerRequest {
    private Long id;
    private String text;
    private Integer grade;
}
