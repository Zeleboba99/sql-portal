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
@Table(name = "db_locations")
public class DbLocation {
    @Id
    @SequenceGenerator(name = "dbLocationSeq", sequenceName = "DB_LOCATION_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dbLocationSeq")
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

    @OneToMany(mappedBy = "dbLocation", cascade = CascadeType.REMOVE)
    private List<Test> tests;

    public DbLocation() {
    }

    public DbLocation(@NotNull String name, @NotNull String path, User author) {
        this.name = name;
        this.path = path;
        this.author = author;
    }
}
