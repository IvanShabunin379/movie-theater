package edu.domain.repository;

import edu.database.ConnectionFactory;
import edu.domain.model.Ticket;
import edu.domain.repository.exception.DataAccessException;
import edu.domain.repository.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class TicketsRepository {
    private static final String FIND_ALL_TEMPLATE = """
            SELECT id,
                   session_id,
                   row,
                   place,
                   price,
                   is_purchased,
                   time_of_purchase,
                   visitor_id
            FROM tickets
            """;
    private static final String FIND_BY_SESSION_TEMPLATE = """
            SELECT id,
                   session_id,
                   row,
                   place,
                   price,
                   is_purchased,
                   time_of_purchase,
                   visitor_id
            FROM tickets
            WHERE session_id = ?
            """;
    private static final String FIND_ALL_PURCHASED_TEMPLATE = """
            SELECT id,
                   session_id,
                   row,
                   place,
                   price,
                   is_purchased,
                   time_of_purchase,
                   visitor_id
            FROM tickets
            WHERE is_purchased = true
            """;
    private static final String FIND_ALL_UNPURCHASED_TEMPLATE = """
            SELECT id,
                   session_id,
                   row,
                   place,
                   price,
                   is_purchased,
                   time_of_purchase,
                   visitor_id
            FROM tickets
            WHERE is_purchased = false
            """;
    private static final String FIND_BY_VISITOR_TEMPLATE = """
            SELECT id,
                   session_id,
                   row,
                   place,
                   price,
                   is_purchased,
                   time_of_purchase,
                   visitor_id
            FROM tickets
            WHERE visitor_id = ?
            """;
    private static final String FIND_BY_ID_TEMPLATE = """
            SELECT id,
                   session_id,
                   row,
                   place,
                   price,
                   is_purchased,
                   time_of_purchase,
                   visitor_id
            FROM tickets
            WHERE id = ?
            """;

    private static final String FIND_BY_SESSION_AND_ROW_AND_PLACE_TEMPLATE = """
            SELECT id,
                   session_id,
                   row,
                   place,
                   price,
                   is_purchased,
                   time_of_purchase,
                   visitor_id
            FROM tickets
            WHERE session_id = ? AND row = ? AND place = ?
            """;

    private static final String SAVE_TEMPLATE = """
            INSERT INTO tickets(session_id,
                                row,
                                place,
                                price,
                                is_purchased,
                                time_of_purchase,
                                visitor_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_TEMPLATE = """
            UPDATE tickets
            SET session_id = ?,
                row = ?,
                place = ?,
                price = ?,
                is_purchased = ?,
                time_of_purchase = ?,
                visitor_id = ?
            WHERE id = ?
            """;
    private static final String DELETE_TEMPLATE = "DELETE FROM tickets WHERE id = ?";

    private Connection connection;
    private final TicketMapper ticketMapper;

    public List<Ticket> findAll() {
        connection = ConnectionFactory.getConnection();

        List<Ticket> tickets = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Ticket ticket = ticketMapper.mapRow(resultSet);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return tickets;
    }

    public List<Ticket> findBySession(int sessionId) {
        connection = ConnectionFactory.getConnection();

        List<Ticket> tickets = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_SESSION_TEMPLATE);
            preparedStatement.setInt(1, sessionId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Ticket ticket = ticketMapper.mapRow(resultSet);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return tickets;
    }

    public List<Ticket> findAllPurchased(int sessionId) {
        connection = ConnectionFactory.getConnection();

        List<Ticket> tickets = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_PURCHASED_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Ticket ticket = ticketMapper.mapRow(resultSet);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return tickets;
    }

    public List<Ticket> findAllUnpurchased(int sessionId) {
        connection = ConnectionFactory.getConnection();

        List<Ticket> tickets = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_UNPURCHASED_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Ticket ticket = ticketMapper.mapRow(resultSet);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return tickets;
    }

    public List<Ticket> findByVisitor(int userId) {
        connection = ConnectionFactory.getConnection();

        List<Ticket> tickets = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_VISITOR_TEMPLATE);
            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Ticket ticket = ticketMapper.mapRow(resultSet);
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return tickets;
    }

    public Optional<Ticket> findById(int id) {
        connection = ConnectionFactory.getConnection();

        Optional<Ticket> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_TEMPLATE);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Ticket ticket = ticketMapper.mapRow(resultSet);
                result = Optional.of(ticket);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return result;
    }

    public Optional<Ticket> findBySessionAndRowAndPlace(int sessionId, int row, int place) {
        connection = ConnectionFactory.getConnection();

        Optional<Ticket> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_SESSION_AND_ROW_AND_PLACE_TEMPLATE);
            preparedStatement.setInt(1, sessionId);
            preparedStatement.setInt(2, row);
            preparedStatement.setInt(3, place);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Ticket ticket = ticketMapper.mapRow(resultSet);
                result = Optional.of(ticket);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return result;
    }

    public boolean save(@NotNull Ticket ticket) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setInt(1, ticket.getSessionId());
            preparedStatement.setInt(2, ticket.getRow());
            preparedStatement.setInt(3, ticket.getPlace());
            preparedStatement.setBigDecimal(4, ticket.getPrice());
            preparedStatement.setBoolean(5, ticket.getIsPurchased());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(ticket.getTimeOfPurchase()));
            preparedStatement.setInt(7, ticket.getVisitorId());

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean update(int id, @NotNull Ticket updatedTicket) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setInt(1, updatedTicket.getSessionId());
            preparedStatement.setInt(2, updatedTicket.getRow());
            preparedStatement.setInt(3, updatedTicket.getPlace());
            preparedStatement.setBigDecimal(4, updatedTicket.getPrice());
            preparedStatement.setBoolean(5, updatedTicket.getIsPurchased());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(updatedTicket.getTimeOfPurchase()));
            preparedStatement.setInt(7, updatedTicket.getVisitorId());
            preparedStatement.setInt(8, id);

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