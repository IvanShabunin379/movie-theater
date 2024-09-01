package edu.domain.repository;

import edu.database.ConnectionFactory;
import edu.domain.model.Director;
import edu.domain.repository.exception.DataAccessException;
import edu.domain.repository.mapper.DirectorMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class DirectorsRepository {
    private static final String FIND_ALL_TEMPLATE = "SELECT id, name FROM directors";
    private static final String FIND_BY_ID_TEMPLATE = "SELECT id, name FROM directors WHERE id = ?";
    private static final String FIND_BY_NAME_TEMPLATE = "SELECT id, name FROM directors WHERE name = ?";
    private static final String SAVE_TEMPLATE = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_TEMPLATE = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_TEMPLATE = "DELETE FROM directors WHERE id = ?";

    private Connection connection;
    private final DirectorMapper directorMapper;

    public List<Director> findAll() {
        connection = ConnectionFactory.getConnection();

        List<Director> directors = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Director director = directorMapper.mapRow(resultSet);
                directors.add(director);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return directors;
    }

    public Optional<Director> findById(int id) {
        connection = ConnectionFactory.getConnection();

        Optional<Director> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_TEMPLATE);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Director director = directorMapper.mapRow(resultSet);
                result = Optional.of(director);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return result;
    }

    public Optional<Director> findByName(String name) {
        connection = ConnectionFactory.getConnection();

        Optional<Director> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NAME_TEMPLATE);
            preparedStatement.setString(1, name);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Director director = directorMapper.mapRow(resultSet);
                result = Optional.of(director);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return result;
    }

    public boolean save(@NotNull Director director) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setString(1, director.getName());

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(int id, @NotNull Director updatedDirector) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setString(1, updatedDirector.getName());
            preparedStatement.setInt(2, id);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
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
            throw new DataAccessException(e);
        }
    }
}