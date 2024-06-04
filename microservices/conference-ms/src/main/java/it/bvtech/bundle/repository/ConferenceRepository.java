package it.bvtech.bundle.repository;

import it.bvtech.bundle.domain.Conference;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Conference entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long>, JpaSpecificationExecutor<Conference> {}
