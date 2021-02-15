package ru.vsu.csf.sqlportal.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Table {
    private String name;
    private List<List<String>> fields = new ArrayList<>();
    private List<List<?>> data = new ArrayList<>();

    public Table() {
    }
}
