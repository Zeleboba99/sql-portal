package ru.vsu.csf.sqlportal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestRequest {
    private String name;
    private Integer number;
    private Long previousTestId;
    private Integer maxAttemptsCnt;
    private DbInfoRequest dbInfoRequest;
}
