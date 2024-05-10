package edu.domain.repository.mapper;

import edu.domain.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorMapper implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet resultSet) throws SQLException  {
        return new Director(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }
}
