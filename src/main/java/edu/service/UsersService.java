package edu.service;

import edu.domain.model.Ticket;
import edu.domain.model.User;
import edu.domain.repository.TicketsRepository;
import edu.domain.repository.UsersRepository;
import edu.service.exception.ticket.TicketNotFoundException;
import edu.service.exception.user.UserAlreadyExistsException;
import edu.service.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;

@RequiredArgsConstructor
public class UsersService {
    private static final Integer MIN_PASSWORD_LEN = 6;

    private final UsersRepository usersRepository;
    private final TicketsRepository ticketsRepository;

    public void register(String name, String email, String passwordHash) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);

        if (!usersRepository.save(user)) {
            throw new UserAlreadyExistsException();
        }
    }

    public void unregister(int id) {
        if (!usersRepository.delete(id)) {
            throw new UserNotFoundException();
        }
    }

    public User getTicketOwner(int ticketId) {
        Ticket ticket = ticketsRepository.findById(ticketId)
                .orElseThrow(TicketNotFoundException::new);

        return usersRepository.findById(ticket.getVisitorId())
                .orElseThrow(UserNotFoundException::new);
    }

    public void updateName(int id, String name) {
        User userWithUpdatedName = usersRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        userWithUpdatedName.setName(name);

        usersRepository.update(id, userWithUpdatedName);
    }

    public void updateEmail(int id, String email) {
        User userWithUpdatedEmail = usersRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        validateEmail(email);

        userWithUpdatedEmail.setEmail(email);

        usersRepository.update(id, userWithUpdatedEmail);
    }

    public void updatePassword(int id, String password) {
        User userWithUpdatedPassword = usersRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        validatePassword(password);

        userWithUpdatedPassword.setPasswordHash(password);

        usersRepository.update(id, userWithUpdatedPassword);
    }

    private void validateEmailAndPassword(String email, String password) {
        validatePassword(password);
        validateEmail(email);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LEN) {
            throw new IllegalArgumentException("Password len should be greater than 5");
        }
    }

    private void validateEmail(String email) {
        EmailValidator emailValidator = EmailValidator.getInstance();
        if (email == null || !emailValidator.isValid(email)) {
            throw new IllegalArgumentException("Invalid email.");
        }
    }
}
