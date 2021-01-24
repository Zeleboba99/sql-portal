package ru.vsu.csf.sqlportal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @SequenceGenerator(name = "courseSeq", sequenceName = "COURSE_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "courseSeq")
    private Long id;
    @NotNull
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<Test> tests;

    public Course() {
    }

    public Course(@NotNull String name, String description, User author) {
        this.name = name;
        this.description = description;
        this.author = author;
    }
}
