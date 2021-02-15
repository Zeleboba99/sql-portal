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
    private Integer maxAttemptsCnt;
    private ExhaustedDBResponse exhaustedDb;
    private CourseResponse course;
    private List<QuestionResponse> questions;
}
