package edu.service;

import edu.domain.model.*;
import edu.domain.repository.*;
import edu.service.exception.auditorium.AuditoriumNotFoundException;
import edu.service.exception.session.SessionAlreadyExistsException;
import edu.service.exception.session.SessionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class SessionsServiceTest {

    private static final int MOVIE_ID = 1;
    private static final int AUDITORIUM_ID = 1;
    private static final int SESSION_ID = 999;
    private static final BigDecimal TICKET_PRICE = BigDecimal.valueOf(10);
    private static final LocalDateTime START_TIME = LocalDateTime.now();

    @Mock
    private SessionsRepository sessionsRepository;
    @Mock
    private TicketsRepository ticketsRepository;
    @Mock
    private AuditoriumsRepository auditoriumsRepository;

    @InjectMocks
    private SessionsService sessionsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldCreateSessionWhenValidData() {
        Auditorium auditorium = new Auditorium(AUDITORIUM_ID, 5, 5, false, false);
        when(auditoriumsRepository.findById(AUDITORIUM_ID)).thenReturn(Optional.of(auditorium));
        when(sessionsRepository.save(any(Session.class))).thenReturn(true);
        when(sessionsRepository.findByMovieAndAuditoriumAndStartTime(MOVIE_ID, AUDITORIUM_ID, START_TIME))
                .thenReturn(Optional.of(new Session(SESSION_ID, MOVIE_ID, AUDITORIUM_ID, START_TIME)));

        sessionsService.create(MOVIE_ID, AUDITORIUM_ID, START_TIME, TICKET_PRICE);

        verify(ticketsRepository, times(25)).save(any(Ticket.class)); // 5 rows * 5 seats
    }

    @Test
    public void shouldThrowSessionAlreadyExistsExceptionWhenSessionAlreadyExists() {
        when(sessionsRepository.save(any(Session.class))).thenReturn(false);

        assertThatThrownBy(() -> sessionsService.create(MOVIE_ID, AUDITORIUM_ID, START_TIME, TICKET_PRICE))
                .isInstanceOf(SessionAlreadyExistsException.class);

        verify(ticketsRepository, never()).save(any(Ticket.class));
    }

    @Test
    public void shouldReturnSessionWhenSessionExists() {
        Session session = new Session(SESSION_ID, MOVIE_ID, AUDITORIUM_ID, START_TIME);
        when(sessionsRepository.findById(SESSION_ID)).thenReturn(Optional.of(session));

        Session result = sessionsService.get(SESSION_ID);

        assertThat(result).isEqualTo(session);
    }

    @Test
    public void shouldThrowSessionNotFoundExceptionWhenSessionNotFound() {
        when(sessionsRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionsService.get(SESSION_ID))
                .isInstanceOf(SessionNotFoundException.class);
    }

    @Test
    public void shouldThrowSessionNotFoundExceptionWhenRemovingNonExistentSession() {
        when(sessionsRepository.findById(SESSION_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionsService.remove(SESSION_ID))
                .isInstanceOf(SessionNotFoundException.class);

        verify(ticketsRepository, never()).delete(anyInt());
    }

    @Test
    public void shouldListAllMovieSessions() {
        Session session1 = new Session(1, MOVIE_ID, AUDITORIUM_ID, START_TIME);
        Session session2 = new Session(2, MOVIE_ID, AUDITORIUM_ID, START_TIME.plusDays(1));
        when(sessionsRepository.findByMovie(MOVIE_ID)).thenReturn(List.of(session1, session2));

        List<Session> sessions = sessionsService.listAllMovieSessions(MOVIE_ID);

        assertThat(sessions).containsExactly(session1, session2);
    }

    @Test
    public void shouldListSessionsForTimePeriodByMovie() {
        LocalDateTime periodStart = LocalDateTime.now();
        LocalDateTime periodEnd = LocalDateTime.now().plusDays(5);
        Session session = new Session(SESSION_ID, MOVIE_ID, AUDITORIUM_ID, START_TIME);
        when(sessionsRepository.findByMovieBetweenTimestamps(MOVIE_ID, periodStart, periodEnd))
                .thenReturn(List.of(session));

        List<Session> result = sessionsService.listAllMovieSessionsForTimePeriodByMovie(MOVIE_ID, periodStart, periodEnd);

        assertThat(result).containsExactly(session);
    }

    @Test
    public void shouldListMovieSessionsForTimePeriod() {
        Movie movie = new Movie(MOVIE_ID, "Movie 1", 2020, 1, "", MovieGenre.ACTION, 120, "", 1, true);
        LocalDateTime periodStart = LocalDateTime.now();
        LocalDateTime periodEnd = LocalDateTime.now().plusDays(5);
        Session session = new Session(SESSION_ID, MOVIE_ID, AUDITORIUM_ID, START_TIME);
        when(sessionsRepository.findByMovieBetweenTimestamps(MOVIE_ID, periodStart, periodEnd))
                .thenReturn(List.of(session));

        Map<Movie, List<Session>> result = sessionsService.listAllMovieSessionsForTimePeriod(
                List.of(movie), periodStart, periodEnd);

        assertThat(result).containsEntry(movie, List.of(session));
    }
}
