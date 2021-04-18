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
@Table(name = "db_info")
public class DbInfo {
    @Id
    @SequenceGenerator(name = "dbInfoSeq", sequenceName = "DB_LOCATION_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dbInfoSeq")
    private Long id;

    @NotNull
    @Column(name="name")
    private String name;

    @Column(name="schema_image")
    private byte[] schemaImage;

    @NotNull
    @Column(name="path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "dbInfo", cascade = CascadeType.REMOVE)
    private List<Test> tests;

    public DbInfo() {
    }

    public DbInfo(@NotNull String name, byte[] schemaImage, @NotNull String path, User author) {
        this.name = name;
        this.schemaImage = schemaImage;
        this.path = path;
        this.author = author;
    }
}
