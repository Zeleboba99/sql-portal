package ru.vsu.csf.sqlportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String text;
    private TestResponse test;
}
