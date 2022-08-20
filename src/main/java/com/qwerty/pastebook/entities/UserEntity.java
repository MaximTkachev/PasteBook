package com.qwerty.pastebook.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {

    public UserEntity(String username, String password) {
        this.username = username;
        this.hashedPassword = password;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String hashedPassword;

    @OneToMany(mappedBy = "owner",
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL)
    private final List<PasteEntity> pastes = new ArrayList<>();
}
