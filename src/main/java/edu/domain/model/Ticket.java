package edu.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
public record Ticket(int id,
                     int sessionId,
                     int row,
                     int place,
                     BigDecimal price,
                     boolean isPurchased,
                     OffsetDateTime timeOfPurchasem,
                     int visitorId
) {
}
