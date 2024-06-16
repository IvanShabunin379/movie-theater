package edu.service;

import edu.domain.model.Country;
import edu.domain.model.Movie;
import edu.domain.repository.CountriesRepository;
import edu.domain.repository.MoviesRepository;
import edu.service.exception.country.CountryAlreadyExistsException;
import edu.service.exception.country.CountryNotFoundException;
import edu.service.exception.movie.MovieNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CountriesService {
    private final CountriesRepository countriesRepository;
    private final MoviesRepository moviesRepository;

    public void add(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Country name should be not null and not empty.");
        }

        Country country = new Country();
        country.setName(name);

        if (!countriesRepository.save(country)) {
            throw new CountryAlreadyExistsException();
        }
    }

    public void remove(int id) {
        if (!countriesRepository.delete(id)) {
            throw new CountryNotFoundException();
        }
    }

    public Country getMovieCountry(int movieId) {
        Movie movie = moviesRepository.findById(movieId)
                .orElseThrow(MovieNotFoundException::new);

        return countriesRepository.findById(movie.getCountryId())
                .orElseThrow(CountryNotFoundException::new);
    }
}