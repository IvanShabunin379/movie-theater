package edu.domain.repository;

import edu.database.ConnectionFactory;
import edu.domain.model.Director;
import edu.domain.repository.exception.DataAccessException;
import edu.domain.repository.mapper.DirectorMapper;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DirectorsRepository {
    private static final String FIND_ALL_TEMPLATE = "SELECT id, name FROM directors";
    private static final String FIND_BY_ID_TEMPLATE = "SELECT id, name FROM directors WHERE id = ?";
    private static final String SAVE_TEMPLATE = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_TEMPLATE = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_TEMPLATE = "DELETE FROM directors WHERE id = ?";

    private final Connection connection;
    private final DirectorMapper directorMapper;

    public DirectorsRepository() {
        connection = ConnectionFactory.getConnection();
        directorMapper = new DirectorMapper();
    }

    public List<Director> findAll() {
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

    public boolean save(@NotNull Director director) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setString(1, director.getName());

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                return false;
            } else {
                throw new DataAccessException(e);
            }
        }
    }

    public boolean update(int id, @NotNull Director updatedDirector) {
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
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEMPLATE);
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
