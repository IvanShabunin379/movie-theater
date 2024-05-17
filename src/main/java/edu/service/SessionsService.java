package edu.service;

import edu.domain.model.Session;
import edu.domain.repository.SessionsRepository;
import edu.service.exception.session.SessionAlreadyExistsException;
import edu.service.exception.session.SessionNotFoundException;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@RequiredArgsConstructor
public class SessionsService {
    private final SessionsRepository sessionsRepository;

    public void create(int movieId, int auditoriumId, OffsetDateTime startTime) {
        Session session = new Session();
        session.setMovieId(movieId);
        session.setAuditoriumId(auditoriumId);
        session.setStartTime(startTime);

        if (!sessionsRepository.save(session)) {
            throw new SessionAlreadyExistsException();
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
