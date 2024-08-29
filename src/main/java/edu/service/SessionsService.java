package edu.service;

import edu.domain.model.Auditorium;
import edu.domain.model.Movie;
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
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RequiredArgsConstructor
public class SessionsService {
    private final SessionsRepository sessionsRepository;
    private final TicketsRepository ticketsRepository;
    private final AuditoriumsRepository auditoriumsRepository;

    public void create(int movieId, int auditoriumId, LocalDateTime startTime, BigDecimal ticketPrice) {
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

    public Session get(int id) {
        return sessionsRepository.findById(id)
                .orElseThrow(SessionNotFoundException::new);
    }

    public void remove(int id) {
        Session removedSession = sessionsRepository.findById(id)
                .orElseThrow(SessionNotFoundException::new);

        List<Ticket> removedTickets = ticketsRepository.findBySession(id);
        removedTickets.forEach(ticket -> ticketsRepository.delete(ticket.getId()));

        if (!sessionsRepository.delete(id)) {
            throw new SessionNotFoundException();
        }
    }

    public List<Session> listAllMovieSessions(int movieId) {
        return sessionsRepository.findByMovie(movieId);
    }

    public List<Session> listAllMovieSessionsForTimePeriodByMovie(int movieId, LocalDateTime periodStart, LocalDateTime periodEnd) {
        return sessionsRepository.findByMovieBetweenTimestamps(movieId, periodStart, periodEnd);
    }

    public Map<Movie, List<Session>> listAllMovieSessionsForTimePeriod(
            List<Movie> movies,
            LocalDateTime periodStart,
            LocalDateTime periodEnd
    ) {
        Map<Movie, List<Session>> result = new TreeMap<>(
                Comparator.comparingInt(Movie::getYear).reversed()
                        .thenComparing(Movie::getName)
        );

        for (Movie movie : movies) {
            List<Session> sessions = listAllMovieSessionsForTimePeriodByMovie(
                    movie.getId(),
                    periodStart,
                    periodEnd
            );
            result.put(movie, sessions);
        }

        return result;
    }
}