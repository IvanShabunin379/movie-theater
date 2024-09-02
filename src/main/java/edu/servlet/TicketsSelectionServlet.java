package edu.servlet;

import edu.domain.model.*;
import edu.domain.repository.*;
import edu.domain.repository.mapper.*;
import edu.service.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/tickets-selection")
public class TicketsSelectionServlet extends HttpServlet {
    private TicketsService ticketsService;
    private SessionsService sessionsService;
    private MoviesService moviesService;
    private AuditoriumsService auditoriumsService;
    private DirectorsService directorsService;
    private CountriesService countriesService;

    @Override
    public void init() throws ServletException {
        ticketsService = new TicketsService(
                new TicketsRepository(new TicketMapper()),
                new UsersRepository(new UserMapper()),
                new SessionsRepository(new SessionMapper()),
                new AuditoriumsRepository(new AuditoriumMapper())
        );
        sessionsService = new SessionsService(
                new SessionsRepository(new SessionMapper()),
                new TicketsRepository(new TicketMapper()),
                new AuditoriumsRepository(new AuditoriumMapper()));
        moviesService = new MoviesService(
                new MoviesRepository(new MovieMapper()),
                new SessionsRepository(new SessionMapper())
        );
        auditoriumsService = new AuditoriumsService(
                new AuditoriumsRepository(new AuditoriumMapper()),
                new SessionsRepository(new SessionMapper())
        );
        directorsService = new DirectorsService(
                new DirectorsRepository(new DirectorMapper()),
                new MoviesRepository(new MovieMapper())
        );
        countriesService = new CountriesService(
                new CountriesRepository(new CountryMapper()),
                new MoviesRepository(new MovieMapper())
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionIdParam = request.getParameter("session_id");
        int sessionId = Integer.parseInt(sessionIdParam);

        Session session = sessionsService.get(sessionId);
        Movie movie = moviesService.getSessionMovie(sessionId);
        Auditorium auditorium = auditoriumsService.getSessionAuditorium(sessionId);
        Country country = countriesService.getMovieCountry(movie.getId());
        Director director = directorsService.getMovieDirector(movie.getId());
        List<Ticket> tickets = ticketsService.getSessionTickets(sessionId);

        request.setAttribute("movie", movie);
        request.setAttribute("auditorium", auditorium);
        request.setAttribute("startTime", session.getStartTime());
        request.setAttribute("countryName", country.getName());
        request.setAttribute("directorName", director.getName());
        request.setAttribute("tickets", tickets);
        request.getRequestDispatcher("/WEB-INF/views/tickets-selection.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] selectedSeats = request.getParameterValues("selected_seats");
        String sessionIdParam = request.getParameter("session_id");

        if (selectedSeats != null) {
            String seatsParam = String.join(",", selectedSeats);

            // Перенаправляем пользователя на страницу подтверждения покупки
            response.sendRedirect("purchase-confirmation?session_id=" + sessionIdParam + "&selected_seats=" + seatsParam);
        } else {
            // Если не выбраны места, возвращаем пользователя на выбор билетов с сообщением об ошибке
            request.setAttribute("errorMessage", "Пожалуйста, выберите хотя бы одно место.");
            request.getRequestDispatcher("/WEB-INF/views/tickets-selection.jsp").forward(request, response);
        }
    }
}
