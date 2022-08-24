package com.qwerty.pastebook.dto.pastes;

import com.qwerty.pastebook.entities.AccessModifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasteDTO {
    private String hash;
    private Long ownerId;
    private String title;
    private String text;
    private Date expiration;
    private AccessModifier accessModifier;
}
