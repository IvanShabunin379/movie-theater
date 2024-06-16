package edu.servlet;

import edu.domain.model.Movie;
import edu.domain.model.Session;
import edu.domain.repository.AuditoriumsRepository;
import edu.domain.repository.MoviesRepository;
import edu.domain.repository.SessionsRepository;
import edu.domain.repository.TicketsRepository;
import edu.service.MoviesService;
import edu.service.SessionsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/schedule")
public class SessionsScheduleServlet extends HttpServlet {
    private final MoviesService moviesService = new MoviesService(new MoviesRepository());
    private final SessionsService sessionsService = new SessionsService(new SessionsRepository(), new TicketsRepository(), new AuditoriumsRepository());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dateParam = request.getParameter("date");
        OffsetDateTime date;

        if (dateParam == null || dateParam.isEmpty()) {
            date = OffsetDateTime.now().plusHours(3).withOffsetSameInstant(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC).toOffsetDateTime();
            dateParam = date.toLocalDate().toString();  // Обновляем dateParam для передачи в JSP
        } else {
            date = OffsetDateTime.parse(dateParam + "T00:00:00Z");
        }

        List<Movie> movies = moviesService.listAllAtBoxOffice();
        Map<Movie, List<Session>> movieSessionsMap = new HashMap<>();

        for (Movie movie : movies) {
            List<Session> sessions = sessionsService.listAllMovieSessionsForTimePeriod(
                    movie.getId(),
                    date,
                    date.plusDays(1));
            movieSessionsMap.put(movie, sessions);
        }

        Map<Movie, List<Date>> movieSessionsMapWithDates = new HashMap<>();
        for (Map.Entry<Movie, List<Session>> entry : movieSessionsMap.entrySet()) {
            List<Date> sessionDates = entry.getValue().stream()
                    .map(session -> Date.from(session.getStartTime().toInstant()))
                    .toList();
            movieSessionsMapWithDates.put(entry.getKey(), sessionDates);
        }

        request.setAttribute("movieSessionsMap", movieSessionsMapWithDates);
        request.setAttribute("selectedDate", dateParam);
        request.getRequestDispatcher("/WEB-INF/views/schedule.jsp").forward(request, response);
    }
}

