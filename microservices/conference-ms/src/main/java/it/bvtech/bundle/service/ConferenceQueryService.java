package it.bvtech.bundle.service;

import it.bvtech.bundle.domain.*; // for static metamodels
import it.bvtech.bundle.domain.Conference;
import it.bvtech.bundle.repository.ConferenceRepository;
import it.bvtech.bundle.service.criteria.ConferenceCriteria;
import it.bvtech.bundle.service.dto.ConferenceDTO;
import it.bvtech.bundle.service.mapper.ConferenceMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Conference} entities in the database.
 * The main input is a {@link ConferenceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link ConferenceDTO} or a {@link Page} of {@link ConferenceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class ConferenceQueryService extends QueryService<Conference> {

    private final Logger log = LoggerFactory.getLogger(ConferenceQueryService.class);

    private final ConferenceRepository conferenceRepository;

    private final ConferenceMapper conferenceMapper;

    public ConferenceQueryService(ConferenceRepository conferenceRepository, ConferenceMapper conferenceMapper) {
        this.conferenceRepository = conferenceRepository;
        this.conferenceMapper = conferenceMapper;
    }

    /**
     * Return a {@link List} of {@link ConferenceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<ConferenceDTO> findByCriteria(ConferenceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Conference> specification = createSpecification(criteria);
        return conferenceMapper.toDto(conferenceRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link ConferenceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<ConferenceDTO> findByCriteria(ConferenceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Conference> specification = createSpecification(criteria);
        return conferenceRepository.findAll(specification, page).map(conferenceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(ConferenceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Conference> specification = createSpecification(criteria);
        return conferenceRepository.count(specification);
    }

    /**
     * Function to convert {@link ConferenceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Conference> createSpecification(ConferenceCriteria criteria) {
        Specification<Conference> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Conference_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Conference_.name));
            }
            if (criteria.getLocation() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLocation(), Conference_.location));
            }
        }
        return specification;
    }
}
