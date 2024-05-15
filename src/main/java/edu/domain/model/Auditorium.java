package edu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auditorium {
    private Integer id;
    private Integer numberOfRows;
    private Integer numberOfSeatsInRow;
    private Boolean is3d;
    private Boolean isVip;
}
