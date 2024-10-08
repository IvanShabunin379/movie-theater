package edu.service;

import edu.domain.model.Auditorium;
import edu.domain.model.Session;
import edu.domain.model.Ticket;
import edu.domain.model.User;
import edu.domain.repository.AuditoriumsRepository;
import edu.domain.repository.SessionsRepository;
import edu.domain.repository.TicketsRepository;
import edu.domain.repository.UsersRepository;
import edu.service.exception.auditorium.AuditoriumNotFoundException;
import edu.service.exception.session.SessionNotFoundException;
import edu.service.exception.ticket.TicketAlreadyExistsException;
import edu.service.exception.ticket.TicketAlreadyPurchasedException;
import edu.service.exception.ticket.TicketNotFoundException;
import edu.service.exception.ticket.TicketNotPurchasedException;
import edu.service.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class TicketsService {
    private static final Integer MIN_PRICE = 0;
    private static final Integer MAX_PRICE = 5000;

    private final TicketsRepository ticketsRepository;
    private final UsersRepository usersRepository;
    private final SessionsRepository sessionsRepository;
    private final AuditoriumsRepository auditoriumsRepository;

    public void create(int sessionId,
                       int row,
                       int place,
                       BigDecimal price,
                       boolean isPurchased,
                       LocalDateTime timeOfPurchase,
                       int visitorId) {
        Ticket ticket = new Ticket();
        ticket.setSessionId(sessionId);
        ticket.setRow(row);
        ticket.setPlace(place);
        ticket.setPrice(price);
        ticket.setIsPurchased(isPurchased);
        ticket.setTimeOfPurchase(timeOfPurchase);
        ticket.setVisitorId(visitorId);

        validateTicketAttributes(ticket);

        if (!ticketsRepository.save(ticket)) {
            throw new TicketAlreadyExistsException();
        }
    }

    public void remove(int id) {
        if (!ticketsRepository.delete(id)) {
            throw new TicketNotFoundException();
        }
    }

    public List<Ticket> listAllSessionTickets(int sessionId) {
        return ticketsRepository.findBySession(sessionId);
    }

    public void updatePrice(int id, BigDecimal newPrice) {
        Ticket ticketWithUpdatedPrice = ticketsRepository.findById(id)
                .orElseThrow(TicketNotFoundException::new);
        ticketWithUpdatedPrice.setPrice(newPrice);

        validateTicketPrice(newPrice);

        ticketsRepository.update(id, ticketWithUpdatedPrice);
    }

    public void buyTicket(int id, LocalDateTime timeOfPurchase, int visitorId) {
        Ticket ticket = ticketsRepository.findById(id)
                .orElseThrow(TicketNotFoundException::new);

        if (ticket.getIsPurchased()) {
            throw new TicketAlreadyPurchasedException();
        }

        User visitor = usersRepository.findById(visitorId)
                .orElseThrow(UserNotFoundException::new);

        ticket.setIsPurchased(true);
        ticket.setTimeOfPurchase(timeOfPurchase);
        ticket.setVisitorId(visitorId);

        ticketsRepository.update(ticket.getId(), ticket);
    }

    public Ticket getTicketBySessionAndSeat(int sessionId, int row, int place) {
        return ticketsRepository.findBySessionAndRowAndPlace(sessionId, row, place)
                .orElseThrow(TicketNotFoundException::new);
    }

    public void handOverTicketToBoxOffice(int id) {
        Ticket ticket = ticketsRepository.findById(id)
                .orElseThrow(TicketNotFoundException::new);

        if (!ticket.getIsPurchased()) {
            throw new TicketNotPurchasedException();
        }

        ticket.setIsPurchased(false);
        ticket.setTimeOfPurchase(null);
        ticket.setVisitorId(null);

        ticketsRepository.update(ticket.getId(), ticket);
    }

    public List<Ticket> getSessionTickets(int sessionId) {
        Session session = sessionsRepository.findById(sessionId)
                .orElseThrow(SessionNotFoundException::new);

        return ticketsRepository.findBySession(sessionId);
    }

    private void validateTicketAttributes(Ticket ticket) {
        validateTicketRowAndPlace(ticket);
        validateTicketPrice(ticket.getPrice());
    }

    private void validateTicketRowAndPlace(Ticket ticket) {
        Session ticketSession = sessionsRepository.findById(ticket.getSessionId())
                .orElseThrow(SessionNotFoundException::new);
        Auditorium auditorium = auditoriumsRepository.findById(ticketSession.getAuditoriumId())
                .orElseThrow(AuditoriumNotFoundException::new);

        if (ticket.getRow() < 0 || ticket.getRow() > auditorium.getNumberOfRows()) {
            throw new IllegalArgumentException("Ticket row should be between 0 and number of rows in auditorium.");
        }

        if (ticket.getPlace() < 1 || ticket.getPlace() > auditorium.getNumberOfSeatsInRow()) {
            throw new IllegalArgumentException("Ticket place should be between 1 and number of seats in row of auditorium.");
        }
    }

    private void validateTicketPrice(BigDecimal price) {
        if (price == null) {
            throw new IllegalArgumentException("Ticket price should be not null.");
        }

        if (price.compareTo(BigDecimal.valueOf(MIN_PRICE)) < 0 || price.compareTo(BigDecimal.valueOf(MAX_PRICE)) > 0) {
            throw new IllegalArgumentException("Ticket price should be between " + MIN_PRICE + " and " + MAX_PRICE + ".");
        }
    }
}