package ru.vsu.csf.sqlportal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "test_id")
    private Test test;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answers;

    public Question() {
    }

    public Question(String text, Test test) {
        this.text = text;
        this.test = test;
    }
}
