package edu.service;

import edu.domain.model.Ticket;
import edu.domain.model.User;
import edu.domain.repository.TicketsRepository;
import edu.domain.repository.UsersRepository;
import edu.pw_hashing.PasswordHasher;
import edu.service.exception.ticket.TicketNotFoundException;
import edu.service.exception.user.UserAlreadyExistsException;
import edu.service.exception.user.WrongPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;
    @Mock
    private TicketsRepository ticketsRepository;
    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private UsersService usersService;

    private final User JOHN_DOE = new User(999, "John Doe", "john.doe@example.com", "hashedpassword");

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterShouldCreateUserWhenValid() {
        String email = "new.email@example.com";
        String password = "strongpassword";
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPasswordHash("hashedpassword");

        when(usersRepository.save(any(User.class))).thenReturn(true);
        when(usersRepository.findByEmail(email)).thenReturn(Optional.of(newUser));
        when(passwordHasher.hash(password)).thenReturn("hashedpassword");

        User registeredUser = usersService.register("New User", email, password);

        assertEquals(email, registeredUser.getEmail());
    }

    @Test
    void testRegisterShouldThrowExceptionIfUserAlreadyExists() {
        String email = JOHN_DOE.getEmail();
        String password = "password";
        when(usersRepository.save(any(User.class))).thenReturn(false);

        assertThrows(UserAlreadyExistsException.class, () -> usersService.register("John Doe", email, password));
    }

    @Test
    void testLoginShouldReturnUserWhenValidCredentials() {
        String password = "password";
        when(usersRepository.findByEmail(JOHN_DOE.getEmail())).thenReturn(Optional.of(JOHN_DOE));
        when(passwordHasher.check(password, JOHN_DOE.getPasswordHash())).thenReturn(true);

        User loggedInUser = usersService.login(JOHN_DOE.getEmail(), password);

        assertEquals(JOHN_DOE.getEmail(), loggedInUser.getEmail());
    }

    @Test
    void testLoginShouldThrowExceptionIfWrongPassword() {
        String password = "wrongpassword";
        when(usersRepository.findByEmail(JOHN_DOE.getEmail())).thenReturn(Optional.of(JOHN_DOE));
        when(passwordHasher.check(password, JOHN_DOE.getPasswordHash())).thenReturn(false);

        assertThrows(WrongPasswordException.class, () -> usersService.login(JOHN_DOE.getEmail(), password));
    }

    @Test
    void testUnregisterShouldDeleteUser() {
        when(usersRepository.delete(JOHN_DOE.getId())).thenReturn(true);

        usersService.unregister(JOHN_DOE.getId());
    }

    @Test
    void testGetTicketOwnerShouldReturnUser() {
        Ticket ticket = new Ticket();
        ticket.setVisitorId(JOHN_DOE.getId());

        when(ticketsRepository.findById(1)).thenReturn(Optional.of(ticket));
        when(usersRepository.findById(JOHN_DOE.getId())).thenReturn(Optional.of(JOHN_DOE));

        User owner = usersService.getTicketOwner(1);

        assertEquals(JOHN_DOE.getId(), owner.getId());
    }

    @Test
    void testGetTicketOwnerShouldThrowExceptionIfTicketNotFound() {
        when(ticketsRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(TicketNotFoundException.class, () -> usersService.getTicketOwner(1));
    }

    @Test
    void testUpdateNameShouldUpdateUserName() {
        when(usersRepository.findById(JOHN_DOE.getId())).thenReturn(Optional.of(JOHN_DOE));

        usersService.updateName(JOHN_DOE.getId(), "Johnathan Doe");

        assertEquals("Johnathan Doe", JOHN_DOE.getName());
    }

    @Test
    void testUpdateEmailShouldUpdateUserEmail() {
        when(usersRepository.findById(JOHN_DOE.getId())).thenReturn(Optional.of(JOHN_DOE));

        usersService.updateEmail(JOHN_DOE.getId(), "new.email@example.com");

        assertEquals("new.email@example.com", JOHN_DOE.getEmail());
    }

    @Test
    void testUpdatePasswordShouldUpdateUserPassword() {
        when(usersRepository.findById(JOHN_DOE.getId())).thenReturn(Optional.of(JOHN_DOE));
        when(passwordHasher.hash("newpassword")).thenReturn("newhashedpassword");

        usersService.updatePassword(JOHN_DOE.getId(), "newpassword");

        assertEquals("newhashedpassword", JOHN_DOE.getPasswordHash());
    }
}
