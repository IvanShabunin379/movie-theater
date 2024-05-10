package edu.domain.repository.mapper;

import edu.domain.model.Auditorium;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuditoriumMapper implements RowMapper<Auditorium> {
    @Override
    public Auditorium mapRow(ResultSet resultSet) throws SQLException {
        return new Auditorium(
                resultSet.getInt("id"),
                resultSet.getInt("number_of_rows"),
                resultSet.getInt("number_of_seats_in_row"),
                resultSet.getBoolean("is_3D"),
                resultSet.getBoolean("is_VIP")
        );
    }
}
