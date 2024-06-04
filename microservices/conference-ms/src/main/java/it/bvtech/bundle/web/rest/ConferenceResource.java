package it.bvtech.bundle.web.rest;

import it.bvtech.bundle.repository.ConferenceRepository;
import it.bvtech.bundle.service.ConferenceQueryService;
import it.bvtech.bundle.service.ConferenceService;
import it.bvtech.bundle.service.criteria.ConferenceCriteria;
import it.bvtech.bundle.service.dto.ConferenceDTO;
import it.bvtech.bundle.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link it.bvtech.bundle.domain.Conference}.
 */
@RestController
@RequestMapping("/api")
public class ConferenceResource {

    private final Logger log = LoggerFactory.getLogger(ConferenceResource.class);

    private static final String ENTITY_NAME = "conferencemsConference";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ConferenceService conferenceService;

    private final ConferenceRepository conferenceRepository;

    private final ConferenceQueryService conferenceQueryService;

    public ConferenceResource(
        ConferenceService conferenceService,
        ConferenceRepository conferenceRepository,
        ConferenceQueryService conferenceQueryService
    ) {
        this.conferenceService = conferenceService;
        this.conferenceRepository = conferenceRepository;
        this.conferenceQueryService = conferenceQueryService;
    }

    /**
     * {@code POST  /conferences} : Create a new conference.
     *
     * @param conferenceDTO the conferenceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new conferenceDTO, or with status {@code 400 (Bad Request)} if the conference has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/conferences")
    public ResponseEntity<ConferenceDTO> createConference(@RequestBody ConferenceDTO conferenceDTO) throws URISyntaxException {
        log.debug("REST request to save Conference : {}", conferenceDTO);
        if (conferenceDTO.getId() != null) {
            throw new BadRequestAlertException("A new conference cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ConferenceDTO result = conferenceService.save(conferenceDTO);
        return ResponseEntity
            .created(new URI("/api/conferences/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /conferences/:id} : Updates an existing conference.
     *
     * @param id the id of the conferenceDTO to save.
     * @param conferenceDTO the conferenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conferenceDTO,
     * or with status {@code 400 (Bad Request)} if the conferenceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the conferenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/conferences/{id}")
    public ResponseEntity<ConferenceDTO> updateConference(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ConferenceDTO conferenceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Conference : {}, {}", id, conferenceDTO);
        if (conferenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conferenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conferenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ConferenceDTO result = conferenceService.update(conferenceDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, conferenceDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /conferences/:id} : Partial updates given fields of an existing conference, field will ignore if it is null
     *
     * @param id the id of the conferenceDTO to save.
     * @param conferenceDTO the conferenceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated conferenceDTO,
     * or with status {@code 400 (Bad Request)} if the conferenceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the conferenceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the conferenceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/conferences/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<ConferenceDTO> partialUpdateConference(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ConferenceDTO conferenceDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Conference partially : {}, {}", id, conferenceDTO);
        if (conferenceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, conferenceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!conferenceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ConferenceDTO> result = conferenceService.partialUpdate(conferenceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, conferenceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /conferences} : get all the conferences.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of conferences in body.
     */
    @GetMapping("/conferences")
    public ResponseEntity<List<ConferenceDTO>> getAllConferences(
        ConferenceCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Conferences by criteria: {}", criteria);
        Page<ConferenceDTO> page = conferenceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /conferences/count} : count all the conferences.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/conferences/count")
    public ResponseEntity<Long> countConferences(ConferenceCriteria criteria) {
        log.debug("REST request to count Conferences by criteria: {}", criteria);
        return ResponseEntity.ok().body(conferenceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /conferences/:id} : get the "id" conference.
     *
     * @param id the id of the conferenceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the conferenceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/conferences/{id}")
    public ResponseEntity<ConferenceDTO> getConference(@PathVariable Long id) {
        log.debug("REST request to get Conference : {}", id);
        Optional<ConferenceDTO> conferenceDTO = conferenceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(conferenceDTO);
    }

    /**
     * {@code DELETE  /conferences/:id} : delete the "id" conference.
     *
     * @param id the id of the conferenceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/conferences/{id}")
    public ResponseEntity<Void> deleteConference(@PathVariable Long id) {
        log.debug("REST request to delete Conference : {}", id);
        conferenceService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
