package edu.domain.model;

import java.time.OffsetDateTime;

public record Session(int id, int movieId, int auditoriumId, OffsetDateTime startTime) {
}
