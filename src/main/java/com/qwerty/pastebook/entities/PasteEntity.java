package com.qwerty.pastebook.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "pastes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasteEntity {

    @Id
    private String hash;

    @ManyToOne
    private UserEntity owner;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Temporal(TemporalType.TIMESTAMP)
    private Date expiration;

    @Enumerated(EnumType.STRING)
    private AccessModifier accessModifier;
}
