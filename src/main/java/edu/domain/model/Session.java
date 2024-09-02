package edu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    private Integer id;
    private Integer movieId;
    private Integer auditoriumId;
    private LocalDateTime startTime;
}
