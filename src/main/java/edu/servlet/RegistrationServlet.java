package edu.servlet;

import edu.domain.model.User;
import edu.domain.repository.TicketsRepository;
import edu.domain.repository.UsersRepository;
import edu.domain.repository.mapper.TicketMapper;
import edu.domain.repository.mapper.UserMapper;
import edu.pw_hashing.PasswordHasher;
import edu.service.UsersService;
import edu.service.exception.user.UserAlreadyExistsException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/registration")
public class RegistrationServlet extends HttpServlet {
    private UsersService usersService;

    @Override
    public void init() throws ServletException {
        usersService = new UsersService(
                new UsersRepository(new UserMapper()),
                new TicketsRepository(new TicketMapper()),
                new PasswordHasher()
        );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/registration.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = usersService.register(name, email, password);
            req.getSession().setAttribute("user", user);
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/successful-authorization.jsp");
            dispatcher.forward(req, resp);
        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/registration.jsp");
            dispatcher.forward(req, resp);
        } catch (UserAlreadyExistsException e) {
            req.setAttribute("errorMessage", "Для введённой Вами почты уже существует аккаунт!"
                    + "Пожалуйста, войдите в систему, если аккаунт Ваш.");
            RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/registration.jsp");
            dispatcher.forward(req, resp);
        }
    }
}