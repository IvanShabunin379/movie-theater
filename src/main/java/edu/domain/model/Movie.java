package edu.domain.model;

public record Movie(int id,
                    String name,
                    int year,
                    int countryId,
                    String posterPath,
                    MovieGenre genre,
                    int duration,
                    String description,
                    int directorId,
                    boolean isCurrentlyAtBoxOffice
) {
}