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
    private Integer number;
    private Integer maxAttemptsCnt;
    private DbInfoResponse dbInfoResponse;
    private CourseResponse course;
    private List<QuestionResponse> questions;
}
