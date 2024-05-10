package edu.domain.repository.mapper;

import edu.domain.model.Country;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CountryMapper implements RowMapper<Country> {
    @Override
    public Country mapRow(ResultSet resultSet) throws SQLException {
        return new Country(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }
}
