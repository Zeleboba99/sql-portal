package ru.vsu.csf.sqlportal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @SequenceGenerator(name = "questionSeq", sequenceName = "QUESTION_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "questionSeq")
    private Long id;
    @Column(name="text")
    private String text;
    @Column(name="right_answer")
    private String rightAnswer;
    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    public Question() {
    }

    public Question(String text, String rightAnswer, Test test) {
        this.text = text;
        this.rightAnswer = rightAnswer;
        this.test = test;
    }
}
