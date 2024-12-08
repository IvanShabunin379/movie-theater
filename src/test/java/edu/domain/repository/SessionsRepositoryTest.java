package edu.domain.repository;

import edu.domain.model.*;
import edu.domain.repository.mapper.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionsRepositoryTest {

    private static final Country COUNTRY_1 = new Country(999, "Country 1");
    private static final Country COUNTRY_2 = new Country(1000, "Country 2");
    private static final Director DIRECTOR_1 = new Director(999, "Director 1");
    private static final Director DIRECTOR_2 = new Director(1000, "Director 2");
    private static final Movie MOVIE_1 = new Movie(999, "Movie", 2010, COUNTRY_1.getId(), "/path/to/poster.jpg", MovieGenre.ACTION, 148, "Bla-bla-bla", DIRECTOR_1.getId(), true);
    private static final Movie MOVIE_2 = new Movie(1000, "No name", 2014, COUNTRY_2.getId(), "/path/to/poster2.jpg", MovieGenre.ADVENTURE, 169, "Tro-lo-lo", DIRECTOR_2.getId(), false);
    private static final Auditorium AUDITORIUM_1 = new Auditorium(999, 20, 35, true, false);
    private static final Auditorium AUDITORIUM_2 = new Auditorium(1000, 30, 30, false, true);
    private static final Session SESSION_1 = new Session(999, MOVIE_1.getId(), AUDITORIUM_1.getId(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    private static final Session SESSION_2 = new Session(1000, MOVIE_2.getId(), AUDITORIUM_2.getId(), LocalDateTime.now().plusDays(2).truncatedTo(ChronoUnit.SECONDS));

    private final CountriesRepository countriesRepository = new CountriesRepository(new CountryMapper());
    private final DirectorsRepository directorsRepository = new DirectorsRepository(new DirectorMapper());
    private final MoviesRepository moviesRepository = new MoviesRepository(new MovieMapper());
    private final AuditoriumsRepository auditoriumsRepository = new AuditoriumsRepository(new AuditoriumMapper());
    private final SessionsRepository sessionsRepository = new SessionsRepository(new SessionMapper());

    @BeforeEach
    public void setUp() {
        countriesRepository.saveById(COUNTRY_1);
        countriesRepository.saveById(COUNTRY_2);

        directorsRepository.saveById(DIRECTOR_1);
        directorsRepository.saveById(DIRECTOR_2);

        moviesRepository.saveById(MOVIE_1);
        moviesRepository.saveById(MOVIE_2);

        auditoriumsRepository.save(AUDITORIUM_1);
        auditoriumsRepository.save(AUDITORIUM_2);

        sessionsRepository.saveById(SESSION_1);
        sessionsRepository.saveById(SESSION_2);
    }

    @AfterEach
    public void tearDown() {
        sessionsRepository.delete(SESSION_1.getId());
        sessionsRepository.delete(SESSION_2.getId());
        moviesRepository.delete(MOVIE_1.getId());
        moviesRepository.delete(MOVIE_2.getId());
        auditoriumsRepository.delete(AUDITORIUM_1.getId());
        auditoriumsRepository.delete(AUDITORIUM_2.getId());
        countriesRepository.delete(COUNTRY_1.getId());
        countriesRepository.delete(COUNTRY_2.getId());
        directorsRepository.delete(DIRECTOR_1.getId());
        directorsRepository.delete(DIRECTOR_2.getId());
    }

    @Test
    public void sessionShouldCanBeFoundByIdWhenItCreated() {
        Session session = sessionsRepository.findById(SESSION_1.getId()).orElse(null);
        assertThat(session).isNotNull();
        assertThat(session).isEqualToComparingOnlyGivenFields(SESSION_1, "id", "movieId", "auditoriumId", "startTime");
        assertThat(session.getStartTime().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(SESSION_1.getStartTime().truncatedTo(ChronoUnit.SECONDS));
    }

    @Test
    public void sessionShouldCanBeUpdatedAndFoundWithUpdatedData() {
        LocalDateTime updatedTime = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.SECONDS);
        Session updatedSession = new Session(SESSION_1.getId(), SESSION_1.getMovieId(), SESSION_1.getAuditoriumId(), updatedTime);

        sessionsRepository.update(SESSION_1.getId(), updatedSession);

        Session session = sessionsRepository.findById(SESSION_1.getId()).orElse(null);

        assertThat(session).isNotNull();
        assertThat(session.getStartTime().truncatedTo(ChronoUnit.SECONDS)).isEqualTo(updatedTime);
    }

    @Test
    public void findAllShouldReturnAllSessions() {
        List<Session> sessions = sessionsRepository.findAll();

        assertThat(sessions).hasSizeGreaterThanOrEqualTo(2);
        assertThat(sessions).contains(SESSION_1);
    }

    @Test
    public void findByMovieShouldReturnAllSessionsForMovie() {
        List<Session> sessions = sessionsRepository.findByMovie(SESSION_1.getMovieId());

        assertThat(sessions).isNotEmpty();
        assertThat(sessions).allMatch(session -> Objects.equals(session.getMovieId(), SESSION_1.getMovieId()));
    }

    @Test
    public void findByDateShouldReturnSessionsForSpecificDate() {
        LocalDateTime testDate = LocalDateTime.now().toLocalDate().atStartOfDay().truncatedTo(ChronoUnit.SECONDS);
        List<Session> sessions = sessionsRepository.findByDate(testDate);

        assertThat(sessions).isNotEmpty();
        assertThat(sessions).anyMatch(session -> session.getStartTime().toLocalDate().equals(testDate.toLocalDate()));
    }
}
