package ru.vsu.csf.sqlportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AttemptResponse {
    private Long id;
    private Date createdAt;
    private Integer mark;
    private TestResponse test;
    private UserResponse author;
    private List<QuestionResponse> questions;
}
