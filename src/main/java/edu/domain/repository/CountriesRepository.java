package edu.domain.repository;

import edu.database.ConnectionFactory;
import edu.domain.model.Country;
import edu.domain.repository.exception.DataAccessException;
import edu.domain.repository.mapper.CountryMapper;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CountriesRepository {
    private static final String FIND_ALL_TEMPLATE = "SELECT id, name FROM countries";
    private static final String FIND_BY_ID_TEMPLATE = "SELECT id, name FROM countries WHERE id = ?";
    private static final String SAVE_TEMPLATE = "INSERT INTO countries(name) VALUES (?)";
    private static final String UPDATE_TEMPLATE = "UPDATE countries SET name = ? WHERE id = ?";
    private static final String DELETE_TEMPLATE = "DELETE FROM countries WHERE id = ?";

    private Connection connection;
    private final CountryMapper countryMapper;

    public CountriesRepository() {
        countryMapper = new CountryMapper();
    }

    public List<Country> findAll() {
        connection = ConnectionFactory.getConnection();

        List<Country> countries = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Country country = countryMapper.mapRow(resultSet);
                countries.add(country);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return countries;
    }

    public Optional<Country> findById(int id) {
        connection = ConnectionFactory.getConnection();

        Optional<Country> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_TEMPLATE);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Country country = countryMapper.mapRow(resultSet);
                result = Optional.of(country);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return result;
    }

    public boolean save(@NotNull Country country) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setString(1, country.getName());

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                return false;
            } else {
                throw new DataAccessException(e);
            }
        }
    }

    public boolean update(int id, @NotNull Country updatedCountry) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setString(1, updatedCountry.getName());
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
