package ru.vsu.csf.sqlportal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestRequest {
    private String name;
    private String maxAttemptsCnt;
    private Long exhaustedDB_id;
}
