package edu.service;

import edu.domain.model.Auditorium;
import edu.domain.model.Session;
import edu.domain.repository.AuditoriumsRepository;
import edu.domain.repository.SessionsRepository;
import edu.service.exception.auditorium.AuditoriumAlreadyExistsException;
import edu.service.exception.auditorium.AuditoriumNotFoundException;
import edu.service.exception.session.SessionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class AuditoriumsServiceTest {
    @Mock
    private AuditoriumsRepository auditoriumsRepository;

    @Mock
    private SessionsRepository sessionsRepository;

    @InjectMocks
    private AuditoriumsService auditoriumsService;

    private final Auditorium testAuditorium = new Auditorium(379, 10, 20, true, false);
    private final Session testSession = new Session(1, 379, 1, null);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddAuditoriumSuccess() {
        when(auditoriumsRepository.save(testAuditorium)).thenReturn(true);

        assertDoesNotThrow(() -> auditoriumsService.add(
                testAuditorium.getId(),
                testAuditorium.getNumberOfRows(),
                testAuditorium.getNumberOfSeatsInRow(),
                testAuditorium.getIs3d(),
                testAuditorium.getIsVip())
        );
    }

    @Test
    void testAddAuditoriumAlreadyExists() {
        when(auditoriumsRepository.save(testAuditorium)).thenReturn(false);

        assertThrows(AuditoriumAlreadyExistsException.class, () -> auditoriumsService.add(
                testAuditorium.getId(),
                testAuditorium.getNumberOfRows(),
                testAuditorium.getNumberOfSeatsInRow(),
                testAuditorium.getIs3d(),
                testAuditorium.getIsVip())
        );
    }

    @Test
    void testRemoveAuditoriumSuccess() {
        when(auditoriumsRepository.delete(testAuditorium.getId())).thenReturn(true);
        assertDoesNotThrow(() -> auditoriumsService.remove(testAuditorium.getId()));
    }

    @Test
    void testRemoveAuditoriumNotFound() {
        when(auditoriumsRepository.delete(testAuditorium.getId())).thenReturn(false);
        assertThrows(AuditoriumNotFoundException.class, () -> auditoriumsService.remove(testAuditorium.getId()));
    }

    @Test
    void testGetSessionAuditoriumSuccess() {
        when(sessionsRepository.findById(testSession.getId())).thenReturn(Optional.of(testSession));
        when(auditoriumsRepository.findById(testSession.getAuditoriumId())).thenReturn(Optional.of(testAuditorium));

        Auditorium result = auditoriumsService.getSessionAuditorium(testSession.getId());

        assertNotNull(result);
        assertEquals(testAuditorium.getId(), result.getId());
    }

    @Test
    void testGetSessionAuditoriumSessionNotFound() {
        when(sessionsRepository.findById(testSession.getId())).thenReturn(Optional.empty());
        assertThrows(SessionNotFoundException.class, () -> auditoriumsService.getSessionAuditorium(testSession.getId()));
    }

    @Test
    void testGetSessionAuditoriumAuditoriumNotFound() {
        when(sessionsRepository.findById(testSession.getId())).thenReturn(Optional.of(testSession));
        when(auditoriumsRepository.findById(testSession.getAuditoriumId())).thenReturn(Optional.empty());

        assertThrows(AuditoriumNotFoundException.class, () -> auditoriumsService.getSessionAuditorium(testSession.getId()));
    }

    @Test
    void testUpdateNumberOfSeatsSuccess() {
        when(auditoriumsRepository.findById(testAuditorium.getId())).thenReturn(Optional.of(testAuditorium));

        assertDoesNotThrow(() -> auditoriumsService.updateNumberOfSeats(
                testAuditorium.getId(), 12, 24)
        );
    }

    @Test
    void testUpdateNumberOfSeatsAuditoriumNotFound() {
        when(auditoriumsRepository.findById(testAuditorium.getId())).thenReturn(Optional.empty());

        assertThrows(AuditoriumNotFoundException.class, () -> auditoriumsService.updateNumberOfSeats(
                testAuditorium.getId(), 12, 24)
        );
    }
}
