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
    private Integer maxAttemptsCnt;

    @ManyToOne
    @JoinColumn(name = "db_location_id")
    private DbLocation dbLocation;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "test", cascade = CascadeType.REMOVE)
    private List<Question> questions;

    @OneToMany(mappedBy = "test", cascade = CascadeType.REMOVE)
    private List<Attempt> attempts;

    public Test() {
    }

    public Test(String name, Integer maxAttemptsCnt, DbLocation dbLocation, Course course, List<Question> questions) {
        this.name = name;
        this.maxAttemptsCnt = maxAttemptsCnt;
        this.dbLocation = dbLocation;
        this.course = course;
        this.questions = questions;
    }
}
