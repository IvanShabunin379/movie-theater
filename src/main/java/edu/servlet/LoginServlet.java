package edu.servlet;

import edu.domain.model.User;
import edu.domain.repository.TicketsRepository;
import edu.domain.repository.UsersRepository;
import edu.domain.repository.mapper.TicketMapper;
import edu.domain.repository.mapper.UserMapper;
import edu.pw_hashing.PasswordHasher;
import edu.service.UsersService;
import edu.service.exception.user.UserNotFoundException;
import edu.service.exception.user.WrongPasswordException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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
        RequestDispatcher dispatcher = req.getRequestDispatcher("/WEB-INF/views/login.jsp");
        dispatcher.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            User user = usersService.login(email, password);
            req.getSession().setAttribute("user", user);
            forwardToPageWithAnAttribute(req, resp, "/WEB-INF/views/successful-authorization.jsp", "user", user);
        } catch (UserNotFoundException e) {
            forwardToPageWithAnAttribute(req, resp, "/WEB-INF/views/registration.jsp", "errorMessage",
                    "Вы ещё не зарегистрированы! Пожалуйста, зарегистрируйтесь.");
        } catch (WrongPasswordException e) {
            forwardToPageWithAnAttribute(req, resp, "/WEB-INF/views/login.jsp", "errorMessage",
                    "Пароль неверный! Попробуйте ещё раз.");
        }
    }

    private void forwardToPageWithAnAttribute(HttpServletRequest req, HttpServletResponse resp,
                                              String page, String attributeName, Object attribute)
            throws ServletException, IOException {
        if (attribute != null) {
            req.setAttribute(attributeName, attribute);
        }
        RequestDispatcher dispatcher = req.getRequestDispatcher(page);
        dispatcher.forward(req, resp);
    }
}
