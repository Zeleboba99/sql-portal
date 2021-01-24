package ru.vsu.csf.sqlportal.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseRequest {
    private String name;
    private String description;
}
