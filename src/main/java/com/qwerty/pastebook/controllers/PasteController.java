package com.qwerty.pastebook.controllers;

import com.qwerty.pastebook.dto.pastes.HashDTO;
import com.qwerty.pastebook.dto.pastes.PasteDTO;
import com.qwerty.pastebook.dto.pastes.SearchPasteDTO;
import com.qwerty.pastebook.dto.pastes.UploadPasteDTO;
import com.qwerty.pastebook.services.PasteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pastes")
@RequiredArgsConstructor
public class PasteController {

    private final PasteService pasteService;

    @GetMapping("/hash/{hash}")
    public PasteDTO getPasteByHash(Authentication authentication, @PathVariable("hash") String hash) {
        return pasteService.getByHash(authentication.getName(), hash);
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public HashDTO uploadPaste(Authentication authentication,
                               @Valid @RequestBody UploadPasteDTO uploadPasteDTO) {
        return pasteService.createPaste(authentication.getName(), uploadPasteDTO);
    }

    @GetMapping("/my")
    public PageImpl<PasteDTO> getMyPastes(Authentication authentication,
                                          @RequestParam(defaultValue = "0") @NotNull Integer pageNumber,
                                          @RequestParam(defaultValue = "10") @NotNull Integer recordsNumber) {
        return pasteService.getMyPastes(authentication.getName(), pageNumber, recordsNumber);
    }

    @GetMapping("/latest")
    public List<PasteDTO> getTenLatestPastes() {
        return pasteService.getTenLatestPastes();
    }

    @GetMapping("/search")
    public PageImpl<PasteDTO> searchForPastes(SearchPasteDTO searchPasteDTO,
                                              @RequestParam(defaultValue = "0") @NotNull Integer pageNumber,
                                              @RequestParam(defaultValue = "10") @NotNull Integer recordsNumber) {
        return pasteService.searchForPastes(searchPasteDTO, pageNumber, recordsNumber);
    }
}

