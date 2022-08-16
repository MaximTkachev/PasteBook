package com.qwerty.pastebook.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private String username;

    @Column
    private String hashedPassword;

    @OneToMany(mappedBy = "owner",
    fetch = FetchType.LAZY,
    cascade = CascadeType.ALL)
    private List<PasteEntity> pastes;
}
