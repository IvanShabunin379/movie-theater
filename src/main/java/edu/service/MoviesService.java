package edu.service;

import edu.domain.model.Movie;
import edu.domain.model.MovieGenre;
import edu.domain.repository.MoviesRepository;
import edu.service.exception.movie.MovieAlreadyExistsException;
import edu.service.exception.movie.MovieNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.Year;
import java.util.List;

@RequiredArgsConstructor
public class MoviesService {
    private static final Integer MIN_YEAR = 1895;
    private static final Integer MIN_DURATION = 5;
    private static final Integer MAX_DURATION = 1000;

    private final MoviesRepository moviesRepository;

    public void add(String name,
                    int year,
                    int countryId,
                    String posterPath,
                    MovieGenre genre,
                    int duration,
                    String description,
                    int directorId,
                    boolean isCurrentlyAtBoxOffice) {
        validateMovieAttributes(year, duration);

        if (name == null) {
            throw new IllegalArgumentException("Movie name should be not null.");
        }

        Movie movie = new Movie();
        movie.setName(name);
        movie.setYear(year);
        movie.setCountryId(countryId);
        movie.setPosterPath(posterPath);
        movie.setGenre(genre);
        movie.setDuration(duration);
        movie.setDescription(description);
        movie.setDirectorId(directorId);
        movie.setIsCurrentlyAtBoxOffice(isCurrentlyAtBoxOffice);

        if (!moviesRepository.save(movie)) {
            throw new MovieAlreadyExistsException();
        }
    }

    public void remove(int id) {
        if (!moviesRepository.delete(id)) {
            throw new MovieNotFoundException();
        }
    }

    public List<Movie> listAllAtBoxOffice() {
        return moviesRepository.findAllAreCurrentlyAtBoxOffice();
    }

    public void updatePoster(int id, String newPosterPath) {
        Movie movieWithUpdatedPoster = moviesRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);
        movieWithUpdatedPoster.setPosterPath(newPosterPath);

        moviesRepository.update(id, movieWithUpdatedPoster);
    }

    public Movie getByNameAndDirectorAndYear(String name, int directorId, int year) {
        return moviesRepository.findByNameAndDirectorAndYear(name, directorId, year)
                .orElseThrow(MovieNotFoundException::new);
    }

    public void updateDescription(int id, String newDescription) {
        Movie movieWithUpdatedDescription = moviesRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);
        movieWithUpdatedDescription.setDescription(newDescription);

        moviesRepository.update(id, movieWithUpdatedDescription);
    }

    public void addToRental(int id) {
        Movie movie = moviesRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);

        if (!movie.getIsCurrentlyAtBoxOffice()) {
            movie.setIsCurrentlyAtBoxOffice(true);
            moviesRepository.update(id, movie);
        }
    }

    public void removeFromRental(int id) {
        Movie movie = moviesRepository.findById(id)
                .orElseThrow(MovieNotFoundException::new);

        if (movie.getIsCurrentlyAtBoxOffice()) {
            movie.setIsCurrentlyAtBoxOffice(false);
            moviesRepository.update(id, movie);
        }
    }

    private void validateMovieAttributes(int year, int duration) {
        if (year < 1895 || Year.of(year).isAfter(Year.now())) {
            throw new IllegalArgumentException("Movie year should be between " + MIN_YEAR + " and current year.");
        }

        if (duration < 5 || duration > 1000) {
            throw new IllegalArgumentException("Movie duration should be between "
                    + MIN_DURATION + " and " + MAX_DURATION + " minutes.");
        }
    }
}