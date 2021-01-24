package ru.vsu.csf.sqlportal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tests")
public class Test {
    @Id
    @SequenceGenerator(name = "testSeq", sequenceName = "TEST_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "testSeq")
    private Long id;
    @Column(name="name")
    private String name;
    @Column(name="max_attempts_cnt")
    private String maxAttemptsCnt;
    @ManyToOne
    @JoinColumn(name = "exhausted_db_id")
    private ExhaustedDb exhaustedDB;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
    @OneToMany(mappedBy = "test", cascade = CascadeType.REMOVE)
    private List<Question> questions;

    public Test() {
    }

    public Test(String name, String maxAttemptsCnt, ExhaustedDb exhaustedDB, Course course, List<Question> questions) {
        this.name = name;
        this.maxAttemptsCnt = maxAttemptsCnt;
        this.exhaustedDB = exhaustedDB;
        this.course = course;
        this.questions = questions;
    }
}
