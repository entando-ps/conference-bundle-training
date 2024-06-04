package it.bvtech.bundle.service.mapper;

import it.bvtech.bundle.domain.Conference;
import it.bvtech.bundle.service.dto.ConferenceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Conference} and its DTO {@link ConferenceDTO}.
 */
@Mapper(componentModel = "spring")
public interface ConferenceMapper extends EntityMapper<ConferenceDTO, Conference> {}
