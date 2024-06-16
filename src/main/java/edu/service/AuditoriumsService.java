package edu.service;

import edu.domain.model.Auditorium;
import edu.domain.model.Session;
import edu.domain.repository.AuditoriumsRepository;
import edu.domain.repository.SessionsRepository;
import edu.service.exception.auditorium.AuditoriumAlreadyExistsException;
import edu.service.exception.auditorium.AuditoriumNotFoundException;
import edu.service.exception.session.SessionNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuditoriumsService {
    private static final Integer MIN_TOTAL_NUMBER_OF_SEATS = 5;
    private static final Integer MAX_TOTAL_NUMBER_OF_SEATS = 1000;

    private final AuditoriumsRepository auditoriumsRepository;
    private final SessionsRepository sessionsRepository;

    public void add(int id, int numberOfRows, int numberOfSeatsInRow, boolean is3d, boolean isVip) {
        validateAuditoriumAttributes(id, numberOfRows, numberOfSeatsInRow);

        Auditorium auditorium = new Auditorium(id, numberOfRows, numberOfSeatsInRow, is3d, isVip);

        if (!auditoriumsRepository.save(auditorium)) {
            throw new AuditoriumAlreadyExistsException();
        }
    }

    public void remove(int id) {
        if (!auditoriumsRepository.delete(id)) {
            throw new AuditoriumNotFoundException();
        }
    }

    public Auditorium getSessionAuditorium(int sessionId) {
        Session session = sessionsRepository.findById(sessionId)
                .orElseThrow(SessionNotFoundException::new);

        return auditoriumsRepository.findById(session.getId())
                .orElseThrow(AuditoriumNotFoundException::new);
    }

    public void updateNumberOfSeats(int id, int newNumberOfRows, int newNumberOfSeatsInRow) {
        validateAuditoriumAttributes(id, newNumberOfRows, newNumberOfSeatsInRow);

        Auditorium updatedAuditorium = auditoriumsRepository.findById(id)
                .orElseThrow(AuditoriumNotFoundException::new);
        updatedAuditorium.setNumberOfRows(newNumberOfRows);
        updatedAuditorium.setNumberOfSeatsInRow(newNumberOfSeatsInRow);

        auditoriumsRepository.update(id, updatedAuditorium);
    }

    private void validateAuditoriumAttributes(int id, int numberOfRows, int numberOfSeatsInRow) {
        if (id < 0) {
            throw new IllegalArgumentException("Auditorium id should be non-negative.");
        }

        if (numberOfRows <= 0 || numberOfSeatsInRow <= 0) {
            throw new IllegalArgumentException("Auditorium Number of rows and number of seats in row should be positive.");
        }

        int totalNumberOfSeats = numberOfRows * numberOfSeatsInRow;
        if (totalNumberOfSeats < MIN_TOTAL_NUMBER_OF_SEATS || totalNumberOfSeats > MAX_TOTAL_NUMBER_OF_SEATS) {
            throw new IllegalArgumentException("Auditorium total number of seats should be between "
                    + MIN_TOTAL_NUMBER_OF_SEATS + " and " + MAX_TOTAL_NUMBER_OF_SEATS + ".");
        }
    }
}