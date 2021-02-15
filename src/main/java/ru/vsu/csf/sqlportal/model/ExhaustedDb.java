package ru.vsu.csf.sqlportal.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "exhausted_dbs")
public class ExhaustedDb {
    @Id
    @SequenceGenerator(name = "exhaustedDbSeq", sequenceName = "EXHAUSTED_DB_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exhaustedDbSeq")
    private Long id;

    @NotNull
    @Column(name="name")
    private String name;

    @NotNull
    @Column(name="path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    public ExhaustedDb() {
    }

    public ExhaustedDb(@NotNull String name, @NotNull String path, User author) {
        this.name = name;
        this.path = path;
        this.author = author;
    }
}
