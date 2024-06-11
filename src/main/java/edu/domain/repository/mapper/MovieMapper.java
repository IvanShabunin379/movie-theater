package edu.domain.repository.mapper;

import edu.domain.model.Movie;
import edu.domain.model.MovieGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieMapper implements RowMapper<Movie> {
    @Override
    public Movie mapRow(ResultSet resultSet) throws SQLException {
        return new Movie(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("year"),
                resultSet.getInt("country_id"),
                resultSet.getString("poster_path"),
                MovieGenre.valueOf(resultSet.getString("genre").toUpperCase()),
                resultSet.getInt("duration"),
                resultSet.getString("description"),
                resultSet.getInt("director_id"),
                resultSet.getBoolean("is_currently_at_box_office")
        );
    }
}
