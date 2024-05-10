package edu.domain.repository;

import edu.domain.model.Director;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DirectorsRepository {
    private static final String TABLE_NAME = "directors";
    private static final String FIND_ALL_TEMPLATE = "SELECT id, name FROM " + TABLE_NAME;
    private static final String FIND_BY_ID_TEMPLATE = "SELECT id, name FROM " + TABLE_NAME + " WHERE id = ?";
    private static final String SAVE_TEMPLATE = "INSERT INTO " + TABLE_NAME + "(name) VALUES (?)";
    private static final String UPDATE_TEMPLATE = "UPDATE " + TABLE_NAME + " SET name = ? WHERE id = ?";
    private static final String DELETE_TEMPLATE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

    private Connection connection;

    public List<Director> findAll() {
        List<Director> directors = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Director director = new Director(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                );

                directors.add(director);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
                Director director = new Director(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                );
                result = Optional.of(director);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public void save(@NotNull Director director) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setString(1, director.name());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(int id, @NotNull Director updatedDirector) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setString(1, updatedDirector.name());
            preparedStatement.setInt(2, id);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEMPLATE);
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
