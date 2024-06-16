package edu.domain.repository;

import edu.database.ConnectionFactory;
import edu.domain.model.Auditorium;
import edu.domain.repository.exception.DataAccessException;
import edu.domain.repository.mapper.AuditoriumMapper;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuditoriumsRepository {
    private static final String FIND_ALL_TEMPLATE = """
            SELECT id,
                   number_of_rows,
                   number_of_seats_in_row,
                   is_3D,
                   is_VIP
            FROM auditoriums
            """;
    private static final String FIND_BY_ID_TEMPLATE = """
            SELECT id,
                  number_of_rows,
                  number_of_seats_in_row,
                  is_3D,
                  is_VIP
            FROM auditoriums
            WHERE id = ?
            """;
    private static final String SAVE_TEMPLATE = """
            INSERT INTO auditoriums(id, number_of_rows, number_of_seats_in_row, is_3D, is_VIP)
            VALUES (?, ?, ?, ?, ?)
            """;
    private static final String UPDATE_TEMPLATE = """
            UPDATE auditoriums
            SET id = ?,
                number_of_rows = ?,
                number_of_seats_in_row = ?,
                is_3D = ?,
                is_VIP = ?
            WHERE id = ?
            """;
    private static final String DELETE_TEMPLATE = "DELETE FROM auditoriums WHERE id = ?";

    private Connection connection;
    private final AuditoriumMapper auditoriumMapper;

    public AuditoriumsRepository() {
        auditoriumMapper = new AuditoriumMapper();
    }

    public List<Auditorium> findAll() {
        connection = ConnectionFactory.getConnection();

        List<Auditorium> auditoriums = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Auditorium auditorium = auditoriumMapper.mapRow(resultSet);
                auditoriums.add(auditorium);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return auditoriums;
    }

    public Optional<Auditorium> findById(int id) {
        connection = ConnectionFactory.getConnection();

        Optional<Auditorium> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_TEMPLATE);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Auditorium auditorium = auditoriumMapper.mapRow(resultSet);
                result = Optional.of(auditorium);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return result;
    }

    public boolean save(@NotNull Auditorium auditorium) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setInt(1, auditorium.getId());
            preparedStatement.setInt(2, auditorium.getNumberOfRows());
            preparedStatement.setInt(3, auditorium.getNumberOfSeatsInRow());
            preparedStatement.setBoolean(4, auditorium.getIs3d());
            preparedStatement.setBoolean(5, auditorium.getIsVip());

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                return false;
            } else {
                throw new DataAccessException(e);
            }
        }
    }

    public boolean update(int id, @NotNull Auditorium updatedAuditorium) {
        connection = ConnectionFactory.getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setInt(1, updatedAuditorium.getId());
            preparedStatement.setInt(2, updatedAuditorium.getNumberOfRows());
            preparedStatement.setInt(3, updatedAuditorium.getNumberOfSeatsInRow());
            preparedStatement.setBoolean(4, updatedAuditorium.getIs3d());
            preparedStatement.setBoolean(5, updatedAuditorium.getIsVip());
            preparedStatement.setInt(6, id);

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