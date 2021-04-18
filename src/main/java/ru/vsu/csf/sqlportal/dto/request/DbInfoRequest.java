package ru.vsu.csf.sqlportal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DbInfoRequest {
    private Long id;
    private String name;
    private Long author_id;
    private String author;
}
