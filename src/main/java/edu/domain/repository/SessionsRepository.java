package edu.domain.repository;

import edu.domain.model.Movie;
import edu.domain.model.Session;
import edu.domain.repository.exception.DataAccessException;
import edu.domain.repository.mapper.SessionMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SessionsRepository {
    private static final String FIND_ALL_TEMPLATE = "SELECT id, movie_id, auditorium_id, start_time FROM sessions";
    private static final String FIND_BY_ID_TEMPLATE = "SELECT id, movie_id, auditorium_id, start_time FROM sessions WHERE id = ?";
    private static final String FIND_BY_MOVIE_TEMPLATE = """
            SELECT id,
                   movie_id,
                   auditorium_id,
                   start_time 
            FROM sessions
            WHERE movie_id = ?
            """;
    private static final String FIND_BY_DAY = """
            SELECT id,
                   movie_id,
                   auditorium_id,
                   start_time 
            FROM sessions
            WHERE DATE_TRUNC('day', start_time) = ?
            """;
    private static final String SAVE_TEMPLATE = "INSERT INTO sessions(movie_id, auditorium_id, start_time) VALUES (?, ?, ?)";
    private static final String UPDATE_TEMPLATE = """
            UPDATE sessions
            SET movie_id = ?,
                auditorium_id = ?,
                start_time = ?
            WHERE id = ?
            """;
    private static final String DELETE_TEMPLATE = "DELETE FROM sessions WHERE id = ?";


    private Connection connection;
    private final SessionMapper sessionMapper;

    public List<Session> findAll() {
        List<Session> sessions = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Session session = sessionMapper.mapRow(resultSet);
                sessions.add(session);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return sessions;
    }

    public List<Session> findByMovie(int movieId) {
        List<Session> sessions = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_MOVIE_TEMPLATE);
            preparedStatement.setInt(1, movieId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Session session = sessionMapper.mapRow(resultSet);
                sessions.add(session);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return sessions;
    }

    public List<Session> findByDay(OffsetDateTime day) {

    }

    public List<Session> findBetweenTimestamps(OffsetDateTime after, OffsetDateTime before) {

    }

    public List<Session> findByMovieBetweenTimestamp(int movieId, OffsetDateTime after, OffsetDateTime before) {

    }

    public Optional<Session> findById(int id) {
        Optional<Session> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_TEMPLATE);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Session session = sessionMapper.mapRow(resultSet);
                result = Optional.of(session);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return result;
    }

    public boolean save(@NotNull Session session) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setInt(1, session.getId());
            preparedStatement.setInt(1, session.getMovieId());
            preparedStatement.setInt(2, session.getAuditoriumId());
            preparedStatement.setTimestamp(3, Timestamp.from(session.getStartTime().toInstant()));

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                return false;
            } else {
                throw new DataAccessException(e);
            }
        }
    }

    public boolean update(int id, @NotNull Session updatedSession) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setInt(1, updatedSession.getMovieId());
            preparedStatement.setInt(2, updatedSession.getAuditoriumId());
            preparedStatement.setTimestamp(3, Timestamp.from(updatedSession.getStartTime().toInstant()));
            preparedStatement.setInt(4, updatedSession.getId());

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public boolean delete(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEMPLATE);
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
