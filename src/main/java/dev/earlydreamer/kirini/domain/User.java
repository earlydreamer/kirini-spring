package dev.earlydreamer.kirini.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_uid")
    private Integer id;

    @Column(name = "user_id", length = 20)
    private String userId;

    @Column(name = "user_password", length = 600)
    private String password;

    @Column(name = "user_name", length = 20)
    private String name;

    @Column(name = "user_email", length = 50)
    private String email;

    @Column(name = "user_introduce", length = 200)
    private String introduce;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_authority")
    private Authority authority;

    @Column(name = "user_point")
    private Integer point;

    @Column(name = "user_icon", columnDefinition = "TEXT")
    private String icon;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Freeboard> freeboards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<FreeboardComment> freeboardComments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserPenalty> penalties = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<KeyboardScore> keyboardScores = new ArrayList<>();

    public enum Authority {
        NORMAL, ARMBAND, ADMIN
    }

    public enum UserStatus {
        ACTIVE, RESTRICTED, SUSPENDED, BANNED
    }
}

