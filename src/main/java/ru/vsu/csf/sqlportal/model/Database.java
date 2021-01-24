package ru.vsu.csf.sqlportal.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Database {
    private String name = "DB";
    private List<Table> tables = new ArrayList<>();

    public Database(String name) {
        this.name = name;
    }
}
