package edu.servlet;

import edu.domain.model.Movie;
import edu.domain.model.Session;
import edu.domain.repository.AuditoriumsRepository;
import edu.domain.repository.MoviesRepository;
import edu.domain.repository.SessionsRepository;
import edu.domain.repository.TicketsRepository;
import edu.domain.repository.mapper.AuditoriumMapper;
import edu.domain.repository.mapper.MovieMapper;
import edu.domain.repository.mapper.SessionMapper;
import edu.domain.repository.mapper.TicketMapper;
import edu.service.MoviesService;
import edu.service.SessionsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@WebServlet("/schedule")
public class SessionsScheduleServlet extends HttpServlet {
    private MoviesService moviesService;
    private SessionsService sessionsService;

    @Override
    public void init() {
        moviesService = new MoviesService(
                new MoviesRepository(new MovieMapper()),
                new SessionsRepository(new SessionMapper())
        );
        sessionsService = new SessionsService(
                new SessionsRepository(new SessionMapper()),
                new TicketsRepository(new TicketMapper()),
                new AuditoriumsRepository(new AuditoriumMapper())
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dateParam = request.getParameter("date");
        LocalDate date;

        if (dateParam == null || dateParam.isEmpty()) {
            date = LocalDate.now(ZoneId.of("UTC+3"));
            dateParam = date.toString();
        } else {
            date = LocalDate.parse(dateParam);
        }

        LocalDateTime startOfDate = date.atStartOfDay();
        LocalDateTime endOfDate = startOfDate.plusDays(1);

        List<Movie> movies = moviesService.listAllAtBoxOffice();
        Map<Movie, List<Session>> movieSessionsMap = sessionsService.listAllMovieSessionsForTimePeriod(movies, startOfDate, endOfDate);

        request.setAttribute("movieSessionsMap", movieSessionsMap);
        request.setAttribute("selectedDate", dateParam);
        request.getRequestDispatcher("/WEB-INF/views/schedule.jsp").forward(request, response);
    }
}
