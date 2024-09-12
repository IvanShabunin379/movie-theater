package edu.domain.repository;

import edu.database.ConnectionFactory;
import edu.domain.model.Movie;
import edu.domain.repository.exception.DataAccessException;
import edu.domain.repository.mapper.MovieMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class MoviesRepository {
    private static final String SAVE_TEMPLATE = """
            INSERT INTO movies(name,
                               year,
                               country_id,
                               poster_path,
                               genre,
                               duration,
                               description,
                               director_id,
                               is_currently_at_box_office)
            VALUES (?, ?, ?, ?, ?::movie_genre, ?, ?, ?, ?)
            """;
    private static final String SAVE_BY_ID_TEMPLATE = """
            INSERT INTO movies(id,
                               name,
                               year,
                               country_id,
                               poster_path,
                               genre,
                               duration,
                               description,
                               director_id,
                               is_currently_at_box_office)
            VALUES (?, ?, ?, ?, ?, ?::movie_genre, ?, ?, ?, ?)
            """;
    private static final String FIND_ALL_TEMPLATE = """
            SELECT id,
                   name,
                   year,
                   country_id,
                   poster_path,
                   genre,
                   duration,
                   description,
                   director_id,
                   is_currently_at_box_office
            FROM movies
            """;
    private static final String FIND_ALL_ARE_CURRENTLY_AT_BOX_OFFICE_TEMPLATE = """
            SELECT id,
                   name,
                   year,
                   country_id,
                   poster_path,
                   genre,
                   duration,
                   description,
                   director_id,
                   is_currently_at_box_office
            FROM movies
            WHERE is_currently_at_box_office = true
            """;
    private static final String FIND_BY_ID_TEMPLATE = """
            SELECT id,
                   name,
                   year,
                   country_id,
                   poster_path,
                   genre,
                   duration,
                   description,
                   director_id,
                   is_currently_at_box_office
            FROM movies
            WHERE id = ?
            """;
    private static final String FIND_BY_NAME_AND_DIRECTOR_AND_YEAR_TEMPLATE = """
            SELECT id,
                   name,
                   year,
                   country_id,
                   poster_path,
                   genre,
                   duration,
                   description,
                   director_id,
                   is_currently_at_box_office
            FROM movies
            WHERE name = ? AND director_id = ? AND year = ?
            """;
    private static final String UPDATE_TEMPLATE = """
            UPDATE movies
            SET name = ?,
                year = ?,
                country_id = ?,
                poster_path = ?,
                genre = ?::movie_genre,
                duration = ?,
                description = ?,
                director_id = ?,
                is_currently_at_box_office = ?
            WHERE id = ?
            """;
    private static final String DELETE_TEMPLATE = "DELETE FROM movies WHERE id = ?";

    private Connection connection;
    private final MovieMapper movieMapper;

    public boolean save(@NotNull Movie movie) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setString(1, movie.getName());
            preparedStatement.setInt(2, movie.getYear());
            preparedStatement.setInt(3, movie.getCountryId());
            preparedStatement.setString(4, movie.getPosterPath());
            if (movie.getGenre() != null){
                preparedStatement.setString(5, String.valueOf(movie.getGenre()).toLowerCase());
            } else {
                preparedStatement.setNull(5, Types.OTHER);
            }
            //preparedStatement.setObject(5, movie.getGenre());
            preparedStatement.setInt(6, movie.getDuration());
            preparedStatement.setString(7, movie.getDescription());
            preparedStatement.setInt(8, movie.getDirectorId());
            preparedStatement.setBoolean(9, movie.getIsCurrentlyAtBoxOffice());

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public void saveById(@NotNull Movie movie) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_BY_ID_TEMPLATE);

            preparedStatement.setInt(1, movie.getId());
            preparedStatement.setString(2, movie.getName());
            preparedStatement.setInt(3, movie.getYear());
            preparedStatement.setInt(4, movie.getCountryId());
            preparedStatement.setString(5, movie.getPosterPath());
            if (movie.getGenre() != null){
                preparedStatement.setString(6, String.valueOf(movie.getGenre()).toLowerCase());
            } else {
                preparedStatement.setNull(6, Types.OTHER);
            }
            //preparedStatement.setObject(6, movie.getGenre());
            preparedStatement.setInt(7, movie.getDuration());
            preparedStatement.setString(8, movie.getDescription());
            preparedStatement.setInt(9, movie.getDirectorId());
            preparedStatement.setBoolean(10, movie.getIsCurrentlyAtBoxOffice());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }
    }

    public List<Movie> findAll() {
        connection = ConnectionFactory.getConnection();

        List<Movie> movies = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Movie movie = movieMapper.mapRow(resultSet);
                movies.add(movie);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return movies;
    }

    public List<Movie> findAllAreCurrentlyAtBoxOffice() {
        connection = ConnectionFactory.getConnection();

        List<Movie> movies = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_ARE_CURRENTLY_AT_BOX_OFFICE_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Movie movie = movieMapper.mapRow(resultSet);
                movies.add(movie);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return movies;
    }

    public Optional<Movie> findById(int id) {
        connection = ConnectionFactory.getConnection();

        Optional<Movie> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_TEMPLATE);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Movie movie = movieMapper.mapRow(resultSet);
                result = Optional.of(movie);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return result;
    }

    public Optional<Movie> findByNameAndDirectorAndYear(String name, int directorId, int year) {
        connection = ConnectionFactory.getConnection();

        Optional<Movie> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NAME_AND_DIRECTOR_AND_YEAR_TEMPLATE);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, directorId);
            preparedStatement.setInt(3, year);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Movie movie = movieMapper.mapRow(resultSet);
                result = Optional.of(movie);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return result;
    }

    public boolean update(int id, @NotNull Movie updatedMovie) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setString(1, updatedMovie.getName());
            preparedStatement.setInt(2, updatedMovie.getYear());
            preparedStatement.setInt(3, updatedMovie.getCountryId());
            preparedStatement.setString(4, updatedMovie.getPosterPath());
            preparedStatement.setString(5, updatedMovie.getGenre().name().toLowerCase());
            preparedStatement.setInt(6, updatedMovie.getDuration());
            preparedStatement.setString(7, updatedMovie.getDescription());
            preparedStatement.setInt(8, updatedMovie.getDirectorId());
            preparedStatement.setBoolean(9, updatedMovie.getIsCurrentlyAtBoxOffice());
            preparedStatement.setInt(10, id);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }
    }

    public boolean delete(int id) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEMPLATE);
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }
    }
}