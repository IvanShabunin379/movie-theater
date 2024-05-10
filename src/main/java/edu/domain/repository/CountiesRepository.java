package edu.domain.repository;

import edu.domain.model.Country;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CountiesRepository {
    private static final String TABLE_NAME = "countries";
    private static final String FIND_ALL_TEMPLATE = "SELECT id, name FROM " + TABLE_NAME;
    private static final String FIND_BY_ID_TEMPLATE = "SELECT id, name FROM " + TABLE_NAME + " WHERE id = ?";
    private static final String SAVE_TEMPLATE = "INSERT INTO " + TABLE_NAME + "(name) VALUES (?)";
    private static final String UPDATE_TEMPLATE = "UPDATE " + TABLE_NAME + " SET name = ? WHERE id = ?";
    private static final String DELETE_TEMPLATE = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

    private Connection connection;

    public List<Country> findAll() {
        List<Country> countries = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Country country = new Country(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                );

                countries.add(country);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return countries;
    }

    public Optional<Country> findById(int id) {
        Optional<Country> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_TEMPLATE);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Country country = new Country(
                        resultSet.getInt("id"),
                        resultSet.getString("name")
                );
                result = Optional.of(country);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public void save(@NotNull Country country) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setString(1, country.name());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean update(int id, @NotNull Country updatedCountry) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setString(1, updatedCountry.name());
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
