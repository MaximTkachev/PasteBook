package com.qwerty.pastebook.services;

import com.qwerty.pastebook.dto.pastes.HashDTO;
import com.qwerty.pastebook.dto.pastes.PasteDTO;
import com.qwerty.pastebook.dto.pastes.SearchPasteDTO;
import com.qwerty.pastebook.dto.pastes.UploadPasteDTO;
import com.qwerty.pastebook.entities.AccessModifier;
import com.qwerty.pastebook.entities.PasteEntity;
import com.qwerty.pastebook.entities.UserEntity;
import com.qwerty.pastebook.exceptions.BadRequestException;
import com.qwerty.pastebook.exceptions.ForbiddenException;
import com.qwerty.pastebook.exceptions.NotFoundException;
import com.qwerty.pastebook.mappers.PasteMapper;
import com.qwerty.pastebook.repositories.PasteRepository;
import com.qwerty.pastebook.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PasteService {

    private final PasteRepository pasteRepository;
    private final UserRepository userRepository;
    private final PasteMapper pasteMapper;
    private final Clock clock;

    @Transactional
    public HashDTO createPaste(String ownerUsername, UploadPasteDTO dto) {
        PasteEntity entity = pasteMapper.DTOToEntity(dto);
        UserEntity owner = getOwnerByUsername(ownerUsername);
        entity.setOwner(owner);
        PasteEntity savedEntity = pasteRepository.save(entity);
        return new HashDTO(savedEntity.getHash());
    }

    @Transactional(readOnly = true)
    public PageImpl<PasteDTO> getMyPastes(String ownerUsername,
                                          Integer pageNumber, Integer recordNumber) {
        Pageable pageRequest = PageRequest.of(pageNumber, recordNumber);
        UserEntity user = getOwnerByUsername(ownerUsername);
        Page<PasteEntity> page = pasteRepository.findAllByOwnerAndExpirationAfterOrExpirationNull(user, new Date(clock.millis()), pageRequest);
        List<PasteDTO> pasteDTOS = page.stream().map(pasteMapper::EntityToDTO).collect(Collectors.toList());
        return new PageImpl<>(pasteDTOS, page.getPageable(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<PasteDTO> getTenLatestPastes() {
        List<PasteEntity> pasteEntities = pasteRepository.getTenLatest();
        return pasteEntities.stream().map(pasteMapper::EntityToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageImpl<PasteDTO> searchForPastes(SearchPasteDTO dto,
                                              Integer pageNumber, Integer recordsNumber) {
        Specification<PasteEntity> specification = (root, query, criteriaBuilder) -> {
            if (dto.getSearchInTexts() && !dto.getSearchInTitles()) {
                return criteriaBuilder.like(root.get("text"), dto.getTemplate());
            }
            if (dto.getSearchInTitles() && ! dto.getSearchInTexts()) {
                return criteriaBuilder.like(root.get("title"), dto.getTemplate());
            }
            if (dto.getSearchInTexts() && dto.getSearchInTitles()) {
                return criteriaBuilder.or(
                        criteriaBuilder.like(root.get("text"), dto.getTemplate()),
                        criteriaBuilder.like(root.get("title"), dto.getTemplate())
                );
            }
            throw new BadRequestException("invalid search conditions");
        };

        PageRequest pageRequest = PageRequest.of(pageNumber, recordsNumber);

        Page<PasteEntity> page = pasteRepository.findAll(specification, pageRequest);
        List<PasteDTO> pastes = page.stream().map(pasteMapper::EntityToDTO).collect(Collectors.toList());
        return new PageImpl<>(pastes, page.getPageable(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PasteDTO getByHash(String ownerUsername, String hash) {
        PasteEntity pasteEntity = pasteRepository.findById(hash)
                .orElseThrow(() -> new NotFoundException("paste not found"));
        if ((pasteEntity.getExpiration() != null && pasteEntity.getExpiration().before(new Date(clock.millis())))
        || (pasteEntity.getAccessModifier() == AccessModifier.PRIVATE && !Objects.equals(pasteEntity.getOwner().getUsername(), ownerUsername))) {
            pasteRepository.deleteById(hash);
            throw new NotFoundException("paste not found");
        }
        return pasteMapper.EntityToDTO(pasteEntity);
    }

    private UserEntity getOwnerByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ForbiddenException("user not found"));
    }
}

