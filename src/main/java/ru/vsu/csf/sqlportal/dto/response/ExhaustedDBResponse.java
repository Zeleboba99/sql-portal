package ru.vsu.csf.sqlportal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExhaustedDBResponse {
    private Long id;
    private String name;
    private Long author_id;
    private String author;
}
