package ru.vsu.csf.sqlportal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;


@Getter
@Setter
@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @SequenceGenerator(name = "answerSeq", sequenceName = "ANSWER_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answerSeq")
    private Long id;

    @Column(name="text")
    private String text;

    @Column(name = "grade")
    private Integer grade;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "attempt_id")
    private Attempt attempt;

    public Answer(String text, Integer grade, User author, Question question, Attempt attempt) {
        this.text = text;
        this.grade = grade;
        this.author = author;
        this.question = question;
        this.attempt = attempt;
    }

    public Answer() {
    }
}
