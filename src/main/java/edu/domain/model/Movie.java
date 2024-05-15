package edu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    private Integer id;
    private String name;
    private Integer year;
    private Integer countryId;
    private String posterPath;
    private MovieGenre genre;
    private Integer duration;
    private String description;
    private Integer directorId;
    private Boolean isCurrentlyAtBoxOffice;
}