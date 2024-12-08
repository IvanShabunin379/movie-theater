
package edu.domain.repository;

import edu.database.ConnectionFactory;
import edu.domain.model.Session;
import edu.domain.repository.exception.DataAccessException;
import edu.domain.repository.mapper.SessionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Log4j2
public class SessionsRepository {
    private static final String SAVE_TEMPLATE = "INSERT INTO sessions(movie_id, auditorium_id, start_time) VALUES (?, ?, ?)";
    private static final String SAVE_BY_ID_TEMPLATE = "INSERT INTO sessions(id, movie_id, auditorium_id, start_time) VALUES (?, ?, ?, ?)";
    private static final String FIND_ALL_TEMPLATE = "SELECT id, movie_id, auditorium_id, start_time FROM sessions";
    private static final String FIND_BY_ID_TEMPLATE = "SELECT id, movie_id, auditorium_id, start_time FROM sessions WHERE id = ?";
    private static final String FIND_BY_MOVIE_AND_AUDITORIUM_AND_START_TIME_TEMPLATE = """
            SELECT id,
                   movie_id,
                   auditorium_id,
                   start_time
            FROM sessions
            WHERE movie_id = ? AND auditorium_id = ? AND start_time = ?
            """;
    private static final String FIND_BY_MOVIE_TEMPLATE = """
            SELECT id,
                   movie_id,
                   auditorium_id,
                   start_time
            FROM sessions
            WHERE movie_id = ?
            """;
    private static final String FIND_BY_DATE_TEMPLATE = """
            SELECT id,
                   movie_id,
                   auditorium_id,
                   start_time
            FROM sessions
            WHERE DATE_TRUNC('day', start_time) = ?
            """;
    private static final String FIND_BY_MOVIE_BETWEEN_TIMESTAMPS_TEMPLATE = """
            SELECT id,
                   movie_id,
                   auditorium_id,
                   start_time
            FROM sessions
            WHERE movie_id = ?
                AND start_time BETWEEN ? AND ?;
            """;
    private static final String FIND_BETWEEN_TIMESTAMPS_TEMPLATE = """
            SELECT id,
                    movie_id,
                    auditorium_id,
                    start_time
             FROM sessions
             WHERE start_time BETWEEN ? AND ?
            """;
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

    public boolean save(@NotNull Session session) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setInt(1, session.getMovieId());
            preparedStatement.setInt(2, session.getAuditoriumId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(session.getStartTime()));

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    public void saveById(@NotNull Session session) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_BY_ID_TEMPLATE);
            preparedStatement.setInt(1, session.getId());
            preparedStatement.setInt(2, session.getMovieId());
            preparedStatement.setInt(3, session.getAuditoriumId());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(session.getStartTime()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }
    }

    public List<Session> findAll() {
        connection = ConnectionFactory.getConnection();

        List<Session> sessions = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Session session = sessionMapper.mapRow(resultSet);
                sessions.add(session);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return sessions;
    }

    public List<Session> findByMovie(int movieId) {
        connection = ConnectionFactory.getConnection();

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
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return sessions;
    }

    public List<Session> findByDate(LocalDateTime date) {
        connection = ConnectionFactory.getConnection();

        List<Session> sessions = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_DATE_TEMPLATE);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(date));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Session session = sessionMapper.mapRow(resultSet);
                sessions.add(session);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return sessions;
    }

    public List<Session> findBetweenTimestamps(LocalDateTime after, LocalDateTime before) {
        connection = ConnectionFactory.getConnection();

        List<Session> sessions = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BETWEEN_TIMESTAMPS_TEMPLATE);
            preparedStatement.setTimestamp(1, Timestamp.valueOf(after));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(before));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Session session = sessionMapper.mapRow(resultSet);
                sessions.add(session);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return sessions;
    }

    public List<Session> findByMovieBetweenTimestamps(int movieId, LocalDateTime after, LocalDateTime before) {
        connection = ConnectionFactory.getConnection();

        List<Session> sessions = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_MOVIE_BETWEEN_TIMESTAMPS_TEMPLATE);
            preparedStatement.setInt(1, movieId);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(after));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(before));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Session session = sessionMapper.mapRow(resultSet);
                sessions.add(session);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return sessions;
    }

    public Optional<Session> findById(int id) {
        connection = ConnectionFactory.getConnection();

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
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return result;
    }

    public Optional<Session> findByMovieAndAuditoriumAndStartTime(int movieId, int auditoriumId, LocalDateTime startTime) {
        connection = ConnectionFactory.getConnection();

        Optional<Session> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_MOVIE_AND_AUDITORIUM_AND_START_TIME_TEMPLATE);
            preparedStatement.setInt(1, movieId);
            preparedStatement.setInt(2, auditoriumId);
            preparedStatement.setTimestamp(3, Timestamp.valueOf(startTime));

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Session session = sessionMapper.mapRow(resultSet);
                result = Optional.of(session);
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            throw new DataAccessException(e);
        }

        return result;
    }

    public boolean update(int id, @NotNull Session updatedSession) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setInt(1, updatedSession.getMovieId());
            preparedStatement.setInt(2, updatedSession.getAuditoriumId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(updatedSession.getStartTime()));
            preparedStatement.setInt(4, updatedSession.getId());

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