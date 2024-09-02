package edu.domain.repository.mapper;

import edu.domain.model.Session;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionMapper implements RowMapper<Session> {
    @Override
    public Session mapRow(ResultSet resultSet) throws SQLException {
        return new Session(
                resultSet.getInt("id"),
                resultSet.getInt("movie_id"),
                resultSet.getInt("auditorium_id"),
                resultSet.getTimestamp("start_time").toLocalDateTime()
        );
    }
}
