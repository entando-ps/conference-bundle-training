package it.bvtech.bundle.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConferenceMapperTest {

    private ConferenceMapper conferenceMapper;

    @BeforeEach
    public void setUp() {
        conferenceMapper = new ConferenceMapperImpl();
    }
}
