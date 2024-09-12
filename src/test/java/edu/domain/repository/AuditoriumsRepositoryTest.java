package edu.domain.repository;

import edu.domain.model.Auditorium;
import edu.domain.repository.mapper.AuditoriumMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AuditoriumsRepositoryTest {
    private static final Auditorium AUDITORIUM_379 = new Auditorium(379, 10, 20, true, false);

    private final AuditoriumsRepository auditoriumsRepository = new AuditoriumsRepository(new AuditoriumMapper());

    @BeforeEach
    public void setUp() {
        auditoriumsRepository.save(AUDITORIUM_379);
    }

    @AfterEach
    public void tearDown() {
        auditoriumsRepository.delete(AUDITORIUM_379.getId());
    }

    @Test
    public void auditoriumShouldBeFoundByIdWhenCreated() {
        Auditorium auditorium = auditoriumsRepository.findById(AUDITORIUM_379.getId()).orElse(null);
        assertThat(auditorium).isNotNull();
        assertThat(auditorium.getId()).isEqualTo(AUDITORIUM_379.getId());
        assertThat(auditorium.getNumberOfRows()).isEqualTo(AUDITORIUM_379.getNumberOfRows());
        assertThat(auditorium.getNumberOfSeatsInRow()).isEqualTo(AUDITORIUM_379.getNumberOfSeatsInRow());
        assertThat(auditorium.getIs3d()).isEqualTo(AUDITORIUM_379.getIs3d());
        assertThat(auditorium.getIsVip()).isEqualTo(AUDITORIUM_379.getIsVip());
    }

    @Test
    public void auditoriumShouldBeUpdatedWhenUpdated() {
        Auditorium updatedAuditorium = new Auditorium(379, 15, 25, false, true);
        auditoriumsRepository.update(AUDITORIUM_379.getId(), updatedAuditorium);

        Auditorium auditorium = auditoriumsRepository.findById(AUDITORIUM_379.getId()).orElse(null);

        assertThat(auditorium).isNotNull();
        assertThat(auditorium.getNumberOfRows()).isEqualTo(updatedAuditorium.getNumberOfRows());
        assertThat(auditorium.getNumberOfSeatsInRow()).isEqualTo(updatedAuditorium.getNumberOfSeatsInRow());
        assertThat(auditorium.getIs3d()).isEqualTo(updatedAuditorium.getIs3d());
        assertThat(auditorium.getIsVip()).isEqualTo(updatedAuditorium.getIsVip());
    }

    @Test
    public void auditoriumShouldNotBeFoundWhenDeleted() {
        auditoriumsRepository.delete(AUDITORIUM_379.getId());

        Auditorium auditorium = auditoriumsRepository.findById(AUDITORIUM_379.getId()).orElse(null);

        assertThat(auditorium).isNull();
    }

    @Test
    public void findAllShouldReturnAllAuditoriums() {
        Auditorium auditorium51 = new Auditorium(51, 12, 22, false, false);
        auditoriumsRepository.save(auditorium51);

        List<Auditorium> auditoriums = auditoriumsRepository.findAll();

        assertThat(auditoriums).hasSizeGreaterThanOrEqualTo(2);
        assertThat(auditoriums).contains(AUDITORIUM_379, auditorium51);

        auditoriumsRepository.delete(auditorium51.getId());
    }
}
