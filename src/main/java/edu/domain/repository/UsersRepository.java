package edu.domain.repository;

import edu.database.ConnectionFactory;
import edu.domain.model.User;
import edu.domain.repository.exception.DataAccessException;
import edu.domain.repository.mapper.UserMapper;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersRepository {
    private static final String FIND_ALL_TEMPLATE = "SELECT id, name, email, password_hash FROM users";
    private static final String FIND_BY_ID_TEMPLATE = "SELECT id, name, email, password_hash FROM users WHERE id = ?";
    private static final String FIND_BY_EMAIL_TEMPLATE = """
            SELECT id, name, email, password_hash
            FROM users
            WHERE email = ?
            """;
    private static final String SAVE_TEMPLATE = "INSERT INTO users(name, email, password_hash) VALUES (?, ?, ?)";
    private static final String UPDATE_TEMPLATE = """
            UPDATE users 
            SET name = ?,
                email = ?,
                password_hash = ?
            WHERE id = ?
            """;
    private static final String DELETE_TEMPLATE = "DELETE FROM users WHERE id = ?";

    private final Connection connection;
    private final UserMapper userMapper;

    public UsersRepository() {
        connection = ConnectionFactory.getConnection();
        userMapper = new UserMapper();
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_TEMPLATE);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = userMapper.mapRow(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return users;
    }

    public Optional<User> findById(int id) {
        Optional<User> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_TEMPLATE);
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = userMapper.mapRow(resultSet);
                result = Optional.of(user);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return result;
    }

    public Optional<User> findByEmail(@NotNull String email) {
        Optional<User> result = Optional.empty();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_EMAIL_TEMPLATE);
            preparedStatement.setString(1, "email");

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = userMapper.mapRow(resultSet);
                result = Optional.of(user);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }

        return result;
    }

    public boolean save(@NotNull User user) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SAVE_TEMPLATE);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                return false;
            } else {
                throw new DataAccessException(e);
            }
        }
    }

    public boolean update(int id, @NotNull User updatedUser) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_TEMPLATE);

            preparedStatement.setString(1, updatedUser.getName());
            preparedStatement.setString(2, updatedUser.getEmail());
            preparedStatement.setString(3, updatedUser.getPasswordHash());
            preparedStatement.setInt(4, id);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public boolean delete(int id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_TEMPLATE);
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
