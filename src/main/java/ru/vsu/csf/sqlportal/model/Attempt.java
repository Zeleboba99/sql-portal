package ru.vsu.csf.sqlportal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "attempts")
public class Attempt {
    @Id
    @SequenceGenerator(name = "attemptSeq", sequenceName = "ATTEMPT_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attemptSeq")
    private Long id;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.REMOVE)
    private List<Answer> answers;

    public Attempt(Date createdAt, Test test, User author, List<Answer> answers) {
        this.createdAt = createdAt;
        this.test = test;
        this.author = author;
        this.answers = answers;
    }

    public Attempt() {
    }
}
