package edu.service;

import edu.domain.model.*;
import edu.domain.repository.*;
import edu.service.exception.session.SessionAlreadyExistsException;
import edu.service.exception.session.SessionNotFoundException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RequiredArgsConstructor
public class SessionsService {
    private final SessionsRepository sessionsRepository;
    private final TicketsRepository ticketsRepository;
    private final AuditoriumsRepository auditoriumsRepository;

    public void create(int movieId, int auditoriumId, OffsetDateTime startTime, BigDecimal ticketPrice) {
        Session session = new Session();
        session.setMovieId(movieId);
        session.setAuditoriumId(auditoriumId);
        session.setStartTime(startTime);

        if (!sessionsRepository.save(session)) {
            throw new SessionAlreadyExistsException();
        }

        Auditorium auditorium = auditoriumsRepository.findById(auditoriumId).get();
        for (int i = 1; i <= auditorium.getNumberOfRows(); ++i) {
            for (int j = 1; j <= auditorium.getNumberOfSeatsInRow(); ++j) {
                Ticket ticket = new Ticket();

                int sessionId = sessionsRepository.findByMovieAndAuditoriumAndStartTime(movieId, auditoriumId, startTime).get().getId();
                ticket.setSessionId(sessionId);
                ticket.setRow(i);
                ticket.setPlace(j);
                ticket.setPrice(ticketPrice);
                ticket.setIsPurchased(false);

                ticketsRepository.save(ticket);
            }
        }
    }

    public void remove(int id) {
        if (!sessionsRepository.delete(id)) {
            throw new SessionNotFoundException();
        }
    }

    public List<Session> listAllMovieSessions(int movieId) {
        return sessionsRepository.findByMovie(movieId);
    }

    public List<Session> listAllMovieSessionsForTimePeriod(int movieId, OffsetDateTime periodStart, OffsetDateTime periodEnd) {
        return sessionsRepository.findByMovieBetweenTimestamps(movieId, periodStart, periodEnd);
    }
}
