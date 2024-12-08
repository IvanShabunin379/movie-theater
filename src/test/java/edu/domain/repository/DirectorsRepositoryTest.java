package edu.domain.repository;

import edu.domain.model.Director;
import edu.domain.repository.mapper.DirectorMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DirectorsRepositoryTest {
    private static final Director PUPKIN = new Director(1, "Vasya Pupkin");

    private final DirectorsRepository directorsRepository = new DirectorsRepository(new DirectorMapper());

    @BeforeEach
    public void setUp() {
        directorsRepository.saveById(PUPKIN);
    }

    @AfterEach
    public void tearDown() {
        directorsRepository.delete(PUPKIN.getId());
    }

    @Test
    public void directorShouldBeFoundByIdWhenCreated() {
        Director director = directorsRepository.findById(PUPKIN.getId()).orElse(null);
        assertThat(director).isNotNull();
        assertThat(director.getId()).isEqualTo(PUPKIN.getId());
        assertThat(director.getName()).isEqualTo(PUPKIN.getName());
    }

    @Test
    public void directorShouldBeFoundByIdWithUpdatedDataWhenUpdated() {
        Director updatedDirector = new Director(1, "Vasiliy Pupkin");
        directorsRepository.update(PUPKIN.getId(), updatedDirector);

        Director director = directorsRepository.findById(PUPKIN.getId()).orElse(null);

        assertThat(director).isNotNull();
        assertThat(director.getName()).isEqualTo(updatedDirector.getName());
    }

    @Test
    public void directorShouldNotBeFoundByIdWhenDeleted() {
        directorsRepository.delete(PUPKIN.getId());

        Director director = directorsRepository.findById(PUPKIN.getId()).orElse(null);

        assertThat(director).isNull();
    }

    @Test
    public void findAllShouldReturnAllDirectors() {
        Director doe = new Director(2, "John Doe");
        directorsRepository.saveById(doe);

        List<Director> directors = directorsRepository.findAll();

        assertThat(directors).hasSizeGreaterThanOrEqualTo(2);
        assertThat(directors).contains(PUPKIN);

        directorsRepository.delete(doe.getId());
    }
}
