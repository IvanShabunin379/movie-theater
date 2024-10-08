package edu.service;

import edu.domain.model.Director;
import edu.domain.model.Movie;
import edu.domain.repository.DirectorsRepository;
import edu.domain.repository.MoviesRepository;
import edu.service.exception.director.DirectorAlreadyExistsException;
import edu.service.exception.director.DirectorNotFoundException;
import edu.service.exception.movie.MovieNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DirectorsService {
    private final DirectorsRepository directorsRepository;
    private final MoviesRepository moviesRepository;

    public void add(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Director name should be not null and not empty.");
        }

        Director director = new Director();
        director.setName(name);

        if (!directorsRepository.save(director)) {
            throw new DirectorAlreadyExistsException();
        }
    }

    public void remove(int id) {
        if (!directorsRepository.delete(id)) {
            throw new DirectorAlreadyExistsException();
        }
    }

    public Director getMovieDirector(int movieId) {
        Movie movie = moviesRepository.findById(movieId)
                .orElseThrow(MovieNotFoundException::new);

        return directorsRepository.findById(movie.getDirectorId())
                .orElseThrow(DirectorNotFoundException::new);
    }

    public Director getByName(String name) {
        return directorsRepository.findByName(name)
                .orElseThrow(DirectorNotFoundException::new);
    }
}