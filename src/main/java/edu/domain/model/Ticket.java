package edu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private Integer id;
    private Integer sessionId;
    private Integer row;
    private Integer place;
    private BigDecimal price;
    private Boolean isPurchased;
    private OffsetDateTime timeOfPurchase;
    private Integer visitorId;
}
