package it.bvtech.bundle.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link it.bvtech.bundle.domain.Conference} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ConferenceDTO implements Serializable {

    private Long id;

    private String name;

    private String location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ConferenceDTO)) {
            return false;
        }

        ConferenceDTO conferenceDTO = (ConferenceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, conferenceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ConferenceDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", location='" + getLocation() + "'" +
            "}";
    }
}
