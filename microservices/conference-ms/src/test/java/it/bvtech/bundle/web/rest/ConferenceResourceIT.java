package it.bvtech.bundle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import it.bvtech.bundle.IntegrationTest;
import it.bvtech.bundle.domain.Conference;
import it.bvtech.bundle.repository.ConferenceRepository;
import it.bvtech.bundle.service.criteria.ConferenceCriteria;
import it.bvtech.bundle.service.dto.ConferenceDTO;
import it.bvtech.bundle.service.mapper.ConferenceMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link ConferenceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ConferenceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LOCATION = "AAAAAAAAAA";
    private static final String UPDATED_LOCATION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/conferences";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ConferenceRepository conferenceRepository;

    @Autowired
    private ConferenceMapper conferenceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restConferenceMockMvc;

    private Conference conference;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conference createEntity(EntityManager em) {
        Conference conference = new Conference().name(DEFAULT_NAME).location(DEFAULT_LOCATION);
        return conference;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Conference createUpdatedEntity(EntityManager em) {
        Conference conference = new Conference().name(UPDATED_NAME).location(UPDATED_LOCATION);
        return conference;
    }

    @BeforeEach
    public void initTest() {
        conference = createEntity(em);
    }

    @Test
    @Transactional
    void createConference() throws Exception {
        int databaseSizeBeforeCreate = conferenceRepository.findAll().size();
        // Create the Conference
        ConferenceDTO conferenceDTO = conferenceMapper.toDto(conference);
        restConferenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(conferenceDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeCreate + 1);
        Conference testConference = conferenceList.get(conferenceList.size() - 1);
        assertThat(testConference.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testConference.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    @Transactional
    void createConferenceWithExistingId() throws Exception {
        // Create the Conference with an existing ID
        conference.setId(1L);
        ConferenceDTO conferenceDTO = conferenceMapper.toDto(conference);

        int databaseSizeBeforeCreate = conferenceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restConferenceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(conferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllConferences() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList
        restConferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conference.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)));
    }

    @Test
    @Transactional
    void getConference() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get the conference
        restConferenceMockMvc
            .perform(get(ENTITY_API_URL_ID, conference.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(conference.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.location").value(DEFAULT_LOCATION));
    }

    @Test
    @Transactional
    void getConferencesByIdFiltering() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        Long id = conference.getId();

        defaultConferenceShouldBeFound("id.equals=" + id);
        defaultConferenceShouldNotBeFound("id.notEquals=" + id);

        defaultConferenceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultConferenceShouldNotBeFound("id.greaterThan=" + id);

        defaultConferenceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultConferenceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllConferencesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where name equals to DEFAULT_NAME
        defaultConferenceShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the conferenceList where name equals to UPDATED_NAME
        defaultConferenceShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllConferencesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where name in DEFAULT_NAME or UPDATED_NAME
        defaultConferenceShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the conferenceList where name equals to UPDATED_NAME
        defaultConferenceShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllConferencesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where name is not null
        defaultConferenceShouldBeFound("name.specified=true");

        // Get all the conferenceList where name is null
        defaultConferenceShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllConferencesByNameContainsSomething() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where name contains DEFAULT_NAME
        defaultConferenceShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the conferenceList where name contains UPDATED_NAME
        defaultConferenceShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllConferencesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where name does not contain DEFAULT_NAME
        defaultConferenceShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the conferenceList where name does not contain UPDATED_NAME
        defaultConferenceShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllConferencesByLocationIsEqualToSomething() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where location equals to DEFAULT_LOCATION
        defaultConferenceShouldBeFound("location.equals=" + DEFAULT_LOCATION);

        // Get all the conferenceList where location equals to UPDATED_LOCATION
        defaultConferenceShouldNotBeFound("location.equals=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllConferencesByLocationIsInShouldWork() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where location in DEFAULT_LOCATION or UPDATED_LOCATION
        defaultConferenceShouldBeFound("location.in=" + DEFAULT_LOCATION + "," + UPDATED_LOCATION);

        // Get all the conferenceList where location equals to UPDATED_LOCATION
        defaultConferenceShouldNotBeFound("location.in=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllConferencesByLocationIsNullOrNotNull() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where location is not null
        defaultConferenceShouldBeFound("location.specified=true");

        // Get all the conferenceList where location is null
        defaultConferenceShouldNotBeFound("location.specified=false");
    }

    @Test
    @Transactional
    void getAllConferencesByLocationContainsSomething() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where location contains DEFAULT_LOCATION
        defaultConferenceShouldBeFound("location.contains=" + DEFAULT_LOCATION);

        // Get all the conferenceList where location contains UPDATED_LOCATION
        defaultConferenceShouldNotBeFound("location.contains=" + UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void getAllConferencesByLocationNotContainsSomething() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        // Get all the conferenceList where location does not contain DEFAULT_LOCATION
        defaultConferenceShouldNotBeFound("location.doesNotContain=" + DEFAULT_LOCATION);

        // Get all the conferenceList where location does not contain UPDATED_LOCATION
        defaultConferenceShouldBeFound("location.doesNotContain=" + UPDATED_LOCATION);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultConferenceShouldBeFound(String filter) throws Exception {
        restConferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(conference.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].location").value(hasItem(DEFAULT_LOCATION)));

        // Check, that the count call also returns 1
        restConferenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultConferenceShouldNotBeFound(String filter) throws Exception {
        restConferenceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restConferenceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingConference() throws Exception {
        // Get the conference
        restConferenceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingConference() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        int databaseSizeBeforeUpdate = conferenceRepository.findAll().size();

        // Update the conference
        Conference updatedConference = conferenceRepository.findById(conference.getId()).get();
        // Disconnect from session so that the updates on updatedConference are not directly saved in db
        em.detach(updatedConference);
        updatedConference.name(UPDATED_NAME).location(UPDATED_LOCATION);
        ConferenceDTO conferenceDTO = conferenceMapper.toDto(updatedConference);

        restConferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, conferenceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(conferenceDTO))
            )
            .andExpect(status().isOk());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeUpdate);
        Conference testConference = conferenceList.get(conferenceList.size() - 1);
        assertThat(testConference.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testConference.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void putNonExistingConference() throws Exception {
        int databaseSizeBeforeUpdate = conferenceRepository.findAll().size();
        conference.setId(count.incrementAndGet());

        // Create the Conference
        ConferenceDTO conferenceDTO = conferenceMapper.toDto(conference);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, conferenceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(conferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchConference() throws Exception {
        int databaseSizeBeforeUpdate = conferenceRepository.findAll().size();
        conference.setId(count.incrementAndGet());

        // Create the Conference
        ConferenceDTO conferenceDTO = conferenceMapper.toDto(conference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConferenceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(conferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamConference() throws Exception {
        int databaseSizeBeforeUpdate = conferenceRepository.findAll().size();
        conference.setId(count.incrementAndGet());

        // Create the Conference
        ConferenceDTO conferenceDTO = conferenceMapper.toDto(conference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConferenceMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(conferenceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateConferenceWithPatch() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        int databaseSizeBeforeUpdate = conferenceRepository.findAll().size();

        // Update the conference using partial update
        Conference partialUpdatedConference = new Conference();
        partialUpdatedConference.setId(conference.getId());

        restConferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConference.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConference))
            )
            .andExpect(status().isOk());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeUpdate);
        Conference testConference = conferenceList.get(conferenceList.size() - 1);
        assertThat(testConference.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testConference.getLocation()).isEqualTo(DEFAULT_LOCATION);
    }

    @Test
    @Transactional
    void fullUpdateConferenceWithPatch() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        int databaseSizeBeforeUpdate = conferenceRepository.findAll().size();

        // Update the conference using partial update
        Conference partialUpdatedConference = new Conference();
        partialUpdatedConference.setId(conference.getId());

        partialUpdatedConference.name(UPDATED_NAME).location(UPDATED_LOCATION);

        restConferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedConference.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedConference))
            )
            .andExpect(status().isOk());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeUpdate);
        Conference testConference = conferenceList.get(conferenceList.size() - 1);
        assertThat(testConference.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testConference.getLocation()).isEqualTo(UPDATED_LOCATION);
    }

    @Test
    @Transactional
    void patchNonExistingConference() throws Exception {
        int databaseSizeBeforeUpdate = conferenceRepository.findAll().size();
        conference.setId(count.incrementAndGet());

        // Create the Conference
        ConferenceDTO conferenceDTO = conferenceMapper.toDto(conference);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restConferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, conferenceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(conferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchConference() throws Exception {
        int databaseSizeBeforeUpdate = conferenceRepository.findAll().size();
        conference.setId(count.incrementAndGet());

        // Create the Conference
        ConferenceDTO conferenceDTO = conferenceMapper.toDto(conference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConferenceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(conferenceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamConference() throws Exception {
        int databaseSizeBeforeUpdate = conferenceRepository.findAll().size();
        conference.setId(count.incrementAndGet());

        // Create the Conference
        ConferenceDTO conferenceDTO = conferenceMapper.toDto(conference);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restConferenceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(conferenceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Conference in the database
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteConference() throws Exception {
        // Initialize the database
        conferenceRepository.saveAndFlush(conference);

        int databaseSizeBeforeDelete = conferenceRepository.findAll().size();

        // Delete the conference
        restConferenceMockMvc
            .perform(delete(ENTITY_API_URL_ID, conference.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Conference> conferenceList = conferenceRepository.findAll();
        assertThat(conferenceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
