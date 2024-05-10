package edu.domain.repository.mapper;

import edu.domain.model.Ticket;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TicketMapper implements RowMapper<Ticket> {
    @Override
    public Ticket mapRow(ResultSet resultSet) throws SQLException {
        return new Ticket(
                resultSet.getInt("id"),
                resultSet.getInt("session_id"),
                resultSet.getInt("row"),
                resultSet.getInt("place"),
                resultSet.getBigDecimal("price"),
                resultSet.getBoolean("is_purchased"),
                OffsetDateTime.ofInstant(resultSet.getTimestamp("time_of_purchase").toInstant(), ZoneOffset.UTC),
                resultSet.getInt("visitor_id")
        );
    }
}
