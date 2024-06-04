package it.bvtech.bundle.service;

import it.bvtech.bundle.domain.Conference;
import it.bvtech.bundle.repository.ConferenceRepository;
import it.bvtech.bundle.service.dto.ConferenceDTO;
import it.bvtech.bundle.service.mapper.ConferenceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Conference}.
 */
@Service
@Transactional
public class ConferenceService {

    private final Logger log = LoggerFactory.getLogger(ConferenceService.class);

    private final ConferenceRepository conferenceRepository;

    private final ConferenceMapper conferenceMapper;

    public ConferenceService(ConferenceRepository conferenceRepository, ConferenceMapper conferenceMapper) {
        this.conferenceRepository = conferenceRepository;
        this.conferenceMapper = conferenceMapper;
    }

    /**
     * Save a conference.
     *
     * @param conferenceDTO the entity to save.
     * @return the persisted entity.
     */
    public ConferenceDTO save(ConferenceDTO conferenceDTO) {
        log.debug("Request to save Conference : {}", conferenceDTO);
        Conference conference = conferenceMapper.toEntity(conferenceDTO);
        conference = conferenceRepository.save(conference);
        return conferenceMapper.toDto(conference);
    }

    /**
     * Update a conference.
     *
     * @param conferenceDTO the entity to save.
     * @return the persisted entity.
     */
    public ConferenceDTO update(ConferenceDTO conferenceDTO) {
        log.debug("Request to update Conference : {}", conferenceDTO);
        Conference conference = conferenceMapper.toEntity(conferenceDTO);
        conference = conferenceRepository.save(conference);
        return conferenceMapper.toDto(conference);
    }

    /**
     * Partially update a conference.
     *
     * @param conferenceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ConferenceDTO> partialUpdate(ConferenceDTO conferenceDTO) {
        log.debug("Request to partially update Conference : {}", conferenceDTO);

        return conferenceRepository
            .findById(conferenceDTO.getId())
            .map(existingConference -> {
                conferenceMapper.partialUpdate(existingConference, conferenceDTO);

                return existingConference;
            })
            .map(conferenceRepository::save)
            .map(conferenceMapper::toDto);
    }

    /**
     * Get all the conferences.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<ConferenceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Conferences");
        return conferenceRepository.findAll(pageable).map(conferenceMapper::toDto);
    }

    /**
     * Get one conference by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ConferenceDTO> findOne(Long id) {
        log.debug("Request to get Conference : {}", id);
        return conferenceRepository.findById(id).map(conferenceMapper::toDto);
    }

    /**
     * Delete the conference by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Conference : {}", id);
        conferenceRepository.deleteById(id);
    }
}
