package ru.vsu.csf.sqlportal.dto.request;

import lombok.Getter;
import lombok.Setter;
import ru.vsu.csf.sqlportal.dto.response.ExhaustedDBResponse;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class TestRequest {
    private String name;
    private Integer maxAttemptsCnt;
    private ExhaustedDbRequest exhaustedDb;
}
