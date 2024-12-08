package edu.domain.repository;

import edu.domain.model.Country;
import edu.domain.model.Director;
import edu.domain.model.Movie;
import edu.domain.model.MovieGenre;
import edu.domain.repository.mapper.CountryMapper;
import edu.domain.repository.mapper.DirectorMapper;
import edu.domain.repository.mapper.MovieMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MoviesRepositoryTest {
    private static final Country COUNTRY_1 = new Country(999, "Country 1");
    private static final Country COUNTRY_2 = new Country(1000, "Country 2");
    private static final Director DIRECTOR_1 = new Director(999, "Director 1");
    private static final Director DIRECTOR_2 = new Director(1000, "Director 2");
    private static final Movie MOVIE_1 = new Movie(999, "Movie", 2010, COUNTRY_1.getId(), "/path/to/poster.jpg", MovieGenre.ACTION, 148, "Bla-bla-bla", DIRECTOR_1.getId(), true);
    private static final Movie MOVIE_2 = new Movie(1000, "No name", 2014, COUNTRY_2.getId(), "/path/to/poster2.jpg", MovieGenre.ADVENTURE, 169, "Tro-lo-lo", DIRECTOR_2.getId(), false);

    private final MoviesRepository moviesRepository = new MoviesRepository(new MovieMapper());
    private final CountriesRepository countriesRepository = new CountriesRepository(new CountryMapper());
    private final DirectorsRepository directorsRepository = new DirectorsRepository(new DirectorMapper());

    @BeforeEach
    public void setUp() {
        countriesRepository.saveById(COUNTRY_1);
        countriesRepository.saveById(COUNTRY_2);
        directorsRepository.saveById(DIRECTOR_1);
        directorsRepository.saveById(DIRECTOR_2);

        moviesRepository.saveById(MOVIE_1);
        moviesRepository.saveById(MOVIE_2);
    }

    @AfterEach
    public void tearDown() {
        moviesRepository.delete(MOVIE_1.getId());
        moviesRepository.delete(MOVIE_2.getId());

        countriesRepository.delete(COUNTRY_1.getId());
        countriesRepository.delete(COUNTRY_2.getId());
        directorsRepository.delete(DIRECTOR_1.getId());
        directorsRepository.delete(DIRECTOR_2.getId());
    }

    @Test
    public void movieShouldBeFoundByIdWhenCreated() {
        Movie movie = moviesRepository.findById(MOVIE_1.getId()).orElse(null);

        assertThat(movie).isNotNull();
        assertThat(movie).isEqualTo(MOVIE_1);
    }

    @Test
    public void movieShouldBeFoundByNameAndDirectorAndYear() {
        Optional<Movie> movie = moviesRepository.findByNameAndDirectorAndYear(MOVIE_1.getName(), MOVIE_1.getDirectorId(), MOVIE_1.getYear());

        assertThat(movie).isPresent();
        assertThat(movie.get()).isEqualTo(MOVIE_1);
    }

    @Test
    public void findAllShouldReturnAllMovies() {
        List<Movie> movies = moviesRepository.findAll();

        assertThat(movies).hasSizeGreaterThanOrEqualTo(2);
        assertThat(movies).contains(MOVIE_1, MOVIE_2);

        moviesRepository.delete(MOVIE_2.getId());
    }

    @Test
    public void findAllAreCurrentlyAtBoxOfficeShouldReturnMoviesAtBoxOffice() {
        List<Movie> movies = moviesRepository.findAllAreCurrentlyAtBoxOffice();

        assertThat(movies).hasSizeGreaterThanOrEqualTo(1);
        assertThat(movies).contains(MOVIE_1);
    }

    @Test
    public void movieShouldBeUpdatedWhenUpdated() {
        Movie updatedMovie = new Movie(999, "Movie Updated", 2010, COUNTRY_1.getId(), "/new/path/to/poster.jpg", MovieGenre.ACTION, 150, "Aha-ha-ha", DIRECTOR_1.getId(), false);
        moviesRepository.update(MOVIE_1.getId(), updatedMovie);

        Optional<Movie> movie = moviesRepository.findById(MOVIE_1.getId());

        assertThat(movie).isPresent();
        assertThat(movie.get()).isEqualTo(updatedMovie);
    }

    @Test
    public void movieShouldNotBeFoundWhenDeleted() {
        moviesRepository.delete(MOVIE_1.getId());

        Optional<Movie> movie = moviesRepository.findById(MOVIE_1.getId());

        assertThat(movie).isEmpty();
    }
}
