package com.qwerty.pastebook.dto.pastes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchPasteDTO {

    @NotNull(message = "template cannot be null")
    private String template;

    private Boolean searchInTitles;
    private Boolean searchInTexts;
}
