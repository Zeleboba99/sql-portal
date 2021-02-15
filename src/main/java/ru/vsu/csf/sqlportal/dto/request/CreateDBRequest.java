package ru.vsu.csf.sqlportal.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateDBRequest {
    private String name;
}
