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
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name = "userSeq", sequenceName = "USER_SEQUENCE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeq")
    private Long id;

    @NotNull
    @Column(name="first_name")
    private String firstName;

    @NotNull
    @Column(name="last_name")
    private String lastName;

    @NotNull
    @Column(name="login")
    private String login;

    @NotNull
    @Column(name="password")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name="role")
    private Role role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Course> courses;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<DbLocation> dbLocations;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Answer> answers;

    @OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE)
    private List<Attempt> attempts;

    public User() {
    }

    public User(@NotNull String firstName, @NotNull String lastName, @NotNull String login, @NotNull String password, @NotNull Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.role = role;
    }
}
