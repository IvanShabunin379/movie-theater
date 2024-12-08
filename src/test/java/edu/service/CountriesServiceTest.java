package edu.service;

import edu.domain.model.Country;
import edu.domain.model.Movie;
import edu.domain.repository.CountriesRepository;
import edu.domain.repository.MoviesRepository;
import edu.service.exception.country.CountryAlreadyExistsException;
import edu.service.exception.country.CountryNotFoundException;
import edu.service.exception.movie.MovieNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CountriesServiceTest {
    @Mock
    private CountriesRepository countriesRepository;
    @Mock
    private MoviesRepository moviesRepository;
    @InjectMocks
    private CountriesService countriesService;

    private final Country testCountry = new Country(1, "USSR");

    private final Movie testMovie = new Movie(1, "Movie Title", 2020, 1, "/path/to/poster",
            null, 120, "Description", 1, true); // Указан countryId = 1

    @BeforeEach
    void mockObjects() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCountrySuccessfully() {
        when(countriesRepository.save(any(Country.class))).thenReturn(true);

        assertDoesNotThrow(() -> countriesService.add("USSR"));
    }


    @Test
    void testAddCountryAlreadyExists() {
        when(countriesRepository.save(any(Country.class))).thenReturn(false);

        assertThrows(CountryAlreadyExistsException.class, () -> countriesService.add("USSR"));
    }

    @Test
    void testAddCountryWithNullNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> countriesService.add(null));
    }

    @Test
    void testRemoveCountrySuccessfully() {
        when(countriesRepository.delete(testCountry.getId())).thenReturn(true);

        countriesService.remove(testCountry.getId());

        verify(countriesRepository, times(1)).delete(testCountry.getId());
    }

    @Test
    void testRemoveCountryNotFound() {
        when(countriesRepository.delete(testCountry.getId())).thenReturn(false);

        assertThrows(CountryNotFoundException.class, () -> countriesService.remove(testCountry.getId()));
    }

    @Test
    void testGetMovieCountrySuccessfully() {
        when(moviesRepository.findById(testMovie.getId())).thenReturn(Optional.of(testMovie));
        when(countriesRepository.findById(testMovie.getCountryId())).thenReturn(Optional.of(testCountry));

        Country country = countriesService.getMovieCountry(testMovie.getId());

        assertEquals(testCountry.getName(), country.getName());
    }

    @Test
    void testGetMovieCountryMovieNotFound() {
        when(moviesRepository.findById(testMovie.getId())).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> countriesService.getMovieCountry(testMovie.getId()));
    }

    @Test
    void testGetMovieCountryCountryNotFound() {
        when(moviesRepository.findById(testMovie.getId())).thenReturn(Optional.of(testMovie));
        when(countriesRepository.findById(testMovie.getCountryId())).thenReturn(Optional.empty());

        assertThrows(CountryNotFoundException.class, () -> countriesService.getMovieCountry(testMovie.getId()));
    }
}
