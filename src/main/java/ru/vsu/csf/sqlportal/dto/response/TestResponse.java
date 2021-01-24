package ru.vsu.csf.sqlportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TestResponse {
    private Long id;
    private String name;
    private String maxAttemptsCnt;
    private ExhaustedDBResponse exhaustedDB;
    private CourseResponse course;
    private List<QuestionResponse> questions;
}
