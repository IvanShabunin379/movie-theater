package edu.servlet;

import edu.domain.model.*;
import edu.domain.repository.*;
import edu.domain.repository.mapper.*;
import edu.service.AuditoriumsService;
import edu.service.MoviesService;
import edu.service.SessionsService;
import edu.service.TicketsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/purchase-confirmation")
public class PurchaseConfirmationServlet extends HttpServlet {
    private TicketsService ticketsService;
    private SessionsService sessionsService;
    private MoviesService moviesService;
    private AuditoriumsService auditoriumsService;

    @Override
    public void init() throws ServletException {
        sessionsService = new SessionsService(
                new SessionsRepository(new SessionMapper()),
                new TicketsRepository(new TicketMapper()),
                new AuditoriumsRepository(new AuditoriumMapper())
        );
        moviesService = new MoviesService(
                new MoviesRepository(new MovieMapper()),
                new SessionsRepository(new SessionMapper())
        );
        auditoriumsService = new AuditoriumsService(
                new AuditoriumsRepository(new AuditoriumMapper()),
                new SessionsRepository(new SessionMapper())
        );
        ticketsService = new TicketsService(
                new TicketsRepository(new TicketMapper()),
                new UsersRepository(new UserMapper()),
                new SessionsRepository(new SessionMapper()),
                new AuditoriumsRepository(new AuditoriumMapper())
        );
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionIdParam = request.getParameter("session_id");
        int sessionId = Integer.parseInt(sessionIdParam);
        String[] selectedSeats = request.getParameter("selected_seats").split(",");

        List<Ticket> purchasedTickets = new ArrayList<>();
        User user = (User) request.getSession().getAttribute("user");

        for (String seat : selectedSeats) {
            String[] rowPlace = seat.split("-");
            int row = Integer.parseInt(rowPlace[0]);
            int place = Integer.parseInt(rowPlace[1]);

            Ticket ticket = ticketsService.getTicketBySessionAndSeat(sessionId, row, place);
            ticketsService.buyTicket(ticket.getId(), LocalDateTime.now(), user.getId());
            purchasedTickets.add(ticket);
        }

        // Передаем данные на страницу подтверждения покупки
        Session session = sessionsService.get(sessionId);
        Movie movie = moviesService.getSessionMovie(sessionId);
        Auditorium auditorium = auditoriumsService.getSessionAuditorium(sessionId);

        request.setAttribute("movie", movie);
        request.setAttribute("auditorium", auditorium);
        request.setAttribute("startTime", session.getStartTime());
        request.setAttribute("tickets", purchasedTickets);
        request.getRequestDispatcher("/WEB-INF/views/purchase-confirmation.jsp").forward(request, response);
    }
}

