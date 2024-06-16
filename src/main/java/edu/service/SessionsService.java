package edu.service;

import edu.domain.model.Auditorium;
import edu.domain.model.Session;
import edu.domain.model.Ticket;
import edu.domain.repository.AuditoriumsRepository;
import edu.domain.repository.SessionsRepository;
import edu.domain.repository.TicketsRepository;
import edu.service.exception.auditorium.AuditoriumNotFoundException;
import edu.service.exception.session.SessionAlreadyExistsException;
import edu.service.exception.session.SessionNotFoundException;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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

        Auditorium auditorium = auditoriumsRepository.findById(auditoriumId)
                .orElseThrow(AuditoriumNotFoundException::new);

        int sessionId = sessionsRepository.findByMovieAndAuditoriumAndStartTime(movieId, auditoriumId, startTime)
                .orElseThrow(SessionNotFoundException::new)
                .getId();
        for (int i = 1; i <= auditorium.getNumberOfRows(); ++i) {
            for (int j = 1; j <= auditorium.getNumberOfSeatsInRow(); ++j) {
                Ticket ticket = new Ticket();

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
