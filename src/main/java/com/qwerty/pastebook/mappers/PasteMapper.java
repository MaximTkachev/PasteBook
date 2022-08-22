package com.qwerty.pastebook.mappers;

import com.qwerty.pastebook.dto.pastes.PasteDTO;
import com.qwerty.pastebook.dto.pastes.UploadPasteDTO;
import com.qwerty.pastebook.entities.PasteEntity;
import com.qwerty.pastebook.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PasteMapper {

    private final ModelMapper mapper;
    private final HashGenerator generator;
    private final Clock clock;

    @PostConstruct
    public void setupMapper() {
        mapper.createTypeMap(UploadPasteDTO.class, PasteEntity.class)
                .addMappings(m -> m.skip(PasteEntity::setExpiration)).setPostConverter(uploadPasteDTOPasteEntityConverter());

        mapper.createTypeMap(PasteEntity.class, PasteDTO.class)
                .addMappings(m -> m.skip(PasteDTO::setOwnerId)).setPostConverter(pasteEntityPasteDTOConverter());
    }

    public PasteDTO EntityToDTO(PasteEntity pasteEntity) {
        return Objects.isNull(pasteEntity) ? null : mapper.map(pasteEntity, PasteDTO.class);
    }

    public PasteEntity DTOToEntity(UploadPasteDTO uploadPasteDTO) {
        return Objects.isNull(uploadPasteDTO) ? null : mapper.map(uploadPasteDTO, PasteEntity.class);
    }

    public Converter<UploadPasteDTO, PasteEntity> uploadPasteDTOPasteEntityConverter() {
        return mappingContext -> {
            UploadPasteDTO source = mappingContext.getSource();
            PasteEntity destination = mappingContext.getDestination();
            mapSpecificFields(source, destination);
            return mappingContext.getDestination();
        };
    }

    public Converter<PasteEntity, PasteDTO> pasteEntityPasteDTOConverter() {
        return mappingContext -> {
            PasteEntity source = mappingContext.getSource();
            PasteDTO destination = mappingContext.getDestination();
            mapSpecificFields(source, destination);
            return mappingContext.getDestination();
        };
    }

    private void mapSpecificFields(UploadPasteDTO source, PasteEntity destination) {
        destination.setHash(generator.generate());
        destination.setDateOfCreation(new Date(clock.millis()));
        source.getExpirationPeriod().duration.ifPresent(integer -> destination.setExpiration(new Date(clock.millis() + integer)));
    }

    private void mapSpecificFields(PasteEntity source, PasteDTO destination) {
        destination.setOwnerId(source.getOwner().getId());
    }
}
