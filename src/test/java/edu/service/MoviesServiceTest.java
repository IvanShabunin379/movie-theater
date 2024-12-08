package edu.service;

import edu.domain.model.Movie;
import edu.domain.model.MovieGenre;
import edu.domain.model.Session;
import edu.domain.repository.MoviesRepository;
import edu.domain.repository.SessionsRepository;
import edu.service.exception.movie.MovieAlreadyExistsException;
import edu.service.exception.movie.MovieNotFoundException;
import edu.service.exception.session.SessionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class MoviesServiceTest {
    @Mock
    private MoviesRepository moviesRepository;
    @Mock
    private SessionsRepository sessionsRepository;
    @InjectMocks
    private MoviesService moviesService;

    private final Movie testMovie = new Movie(999, "Movie", 2010, 999, "/path/to/poster.jpg", MovieGenre.ACTION, 148, "Description", 1, true);
    private final Session testSession = new Session(1, 999, 1, null);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddMovieAlreadyExists() {
        when(moviesRepository.save(testMovie)).thenReturn(false);

        assertThrows(MovieAlreadyExistsException.class, () ->
                moviesService.add(testMovie.getName(), testMovie.getYear(), testMovie.getCountryId(), testMovie.getPosterPath(),
                        testMovie.getGenre(), testMovie.getDuration(), testMovie.getDescription(), testMovie.getDirectorId(), testMovie.getIsCurrentlyAtBoxOffice()));
    }

    @Test
    void testRemoveMovieNotFound() {
        when(moviesRepository.delete(testMovie.getId())).thenReturn(false);

        assertThrows(MovieNotFoundException.class, () -> moviesService.remove(testMovie.getId()));
    }

    @Test
    void testGetSessionMovie() {
        when(sessionsRepository.findById(testSession.getId())).thenReturn(Optional.of(testSession));
        when(moviesRepository.findById(testSession.getMovieId())).thenReturn(Optional.of(testMovie));

        Movie result = moviesService.getSessionMovie(testSession.getId());

        assertThat(result).isEqualTo(testMovie);
    }

    @Test
    void testGetSessionMovieSessionNotFound() {
        when(sessionsRepository.findById(testSession.getId())).thenReturn(Optional.empty());

        assertThrows(SessionNotFoundException.class, () -> moviesService.getSessionMovie(testSession.getId()));
    }

    @Test
    void testGetSessionMovieMovieNotFound() {
        when(sessionsRepository.findById(testSession.getId())).thenReturn(Optional.of(testSession));
        when(moviesRepository.findById(testSession.getMovieId())).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> moviesService.getSessionMovie(testSession.getId()));
    }

    @Test
    void testListAllAtBoxOffice() {
        when(moviesRepository.findAllAreCurrentlyAtBoxOffice()).thenReturn(List.of(testMovie));

        List<Movie> moviesAtBoxOffice = moviesService.listAllAtBoxOffice();

        assertThat(moviesAtBoxOffice).containsExactly(testMovie);
    }

    @Test
    void testUpdatePoster() {
        String newPosterPath = "/new/path/to/poster.jpg";
        when(moviesRepository.findById(testMovie.getId())).thenReturn(Optional.of(testMovie));

        moviesService.updatePoster(testMovie.getId(), newPosterPath);

        assertThat(testMovie.getPosterPath()).isEqualTo(newPosterPath);
    }

    @Test
    void testUpdateDescription() {
        String newDescription = "Updated description";
        when(moviesRepository.findById(testMovie.getId())).thenReturn(Optional.of(testMovie));

        moviesService.updateDescription(testMovie.getId(), newDescription);

        assertThat(testMovie.getDescription()).isEqualTo(newDescription);
    }

    @Test
    void testAddToRental() {
        when(moviesRepository.findById(testMovie.getId())).thenReturn(Optional.of(testMovie));

        moviesService.addToRental(testMovie.getId());

        assertThat(testMovie.getIsCurrentlyAtBoxOffice()).isTrue();
    }

    @Test
    void testRemoveFromRental() {
        when(moviesRepository.findById(testMovie.getId())).thenReturn(Optional.of(testMovie));

        moviesService.removeFromRental(testMovie.getId());

        assertThat(testMovie.getIsCurrentlyAtBoxOffice()).isFalse();
    }
}
