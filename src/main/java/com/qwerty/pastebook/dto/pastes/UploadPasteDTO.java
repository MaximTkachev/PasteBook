package com.qwerty.pastebook.dto.pastes;

import com.qwerty.pastebook.entities.AccessModifier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadPasteDTO {

    private String title;

    @NotNull(message = "text cannot be null")
    private String text;

    @NotNull(message = "access modifier cannot be null")
    private AccessModifier accessModifier;

    @NotNull(message = "expiration period cannot be null")
    private ExpirationPeriod expirationPeriod;
}
