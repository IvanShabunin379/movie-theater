package edu.service;

import edu.domain.model.Director;
import edu.domain.model.Movie;
import edu.domain.repository.DirectorsRepository;
import edu.domain.repository.MoviesRepository;
import edu.service.exception.director.DirectorAlreadyExistsException;
import edu.service.exception.director.DirectorNotFoundException;
import edu.service.exception.movie.MovieNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DirectorsServiceTest {

    @Mock
    private DirectorsRepository directorsRepository;

    @Mock
    private MoviesRepository moviesRepository;

    @InjectMocks
    private DirectorsService directorsService;

    private final Director director = new Director(1, "Vasya Pupkin");
    private final Movie movie = new Movie(1, "Test Movie", 2023, 1, "/path", null, 120, "Description", 1, true);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddDirectorSuccessfully() {
        when(directorsRepository.save(any(Director.class))).thenReturn(true);

        assertDoesNotThrow(() -> directorsService.add(director.getName()));

    }


    @Test
    void testAddDirectorThrowsExceptionIfExists() {
        when(directorsRepository.save(director)).thenReturn(false);
        assertThrows(DirectorAlreadyExistsException.class, () -> directorsService.add(director.getName()));
    }

    @Test
    void testRemoveDirectorSuccessfully() {
        when(directorsRepository.delete(director.getId())).thenReturn(true);
        directorsService.remove(director.getId());
    }

    @Test
    void testRemoveDirectorThrowsExceptionIfNotFound() {
        when(directorsRepository.delete(director.getId())).thenReturn(false);
        assertThrows(DirectorAlreadyExistsException.class, () -> directorsService.remove(director.getId()));
    }

    @Test
    void testGetMovieDirectorSuccessfully() {
        when(moviesRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(directorsRepository.findById(movie.getDirectorId())).thenReturn(Optional.of(director));

        Director result = directorsService.getMovieDirector(movie.getId());
        assertEquals(director, result);
    }

    @Test
    void testGetMovieDirectorThrowsMovieNotFoundException() {
        when(moviesRepository.findById(movie.getId())).thenReturn(Optional.empty());
        assertThrows(MovieNotFoundException.class, () -> directorsService.getMovieDirector(movie.getId()));
    }

    @Test
    void testGetMovieDirectorThrowsDirectorNotFoundException() {
        when(moviesRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
        when(directorsRepository.findById(movie.getDirectorId())).thenReturn(Optional.empty());
        assertThrows(DirectorNotFoundException.class, () -> directorsService.getMovieDirector(movie.getId()));
    }

    @Test
    void testGetByNameSuccessfully() {
        when(directorsRepository.findByName(director.getName())).thenReturn(Optional.of(director));
        Director result = directorsService.getByName(director.getName());
        assertEquals(director, result);
    }

    @Test
    void testGetByNameThrowsDirectorNotFoundException() {
        when(directorsRepository.findByName(director.getName())).thenReturn(Optional.empty());
        assertThrows(DirectorNotFoundException.class, () -> directorsService.getByName(director.getName()));
    }
}
