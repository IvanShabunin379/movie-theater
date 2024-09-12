package edu.domain.repository;

import edu.domain.model.User;
import edu.domain.repository.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UsersRepositoryTest {
    private final UsersRepository usersRepository = new UsersRepository(new UserMapper());
    private static final User JOHN_DOE = new User(999, "John Doe", "john.doe@example.com", "hashedpassword");

    @BeforeEach
    public void setUp() {
        usersRepository.saveById(JOHN_DOE);
    }

    @AfterEach
    public void tearDown() {
        usersRepository.delete(JOHN_DOE.getId());
    }

    @Test
    public void userShouldBeFoundByIdWhenCreated() {
        User user = usersRepository.findById(JOHN_DOE.getId()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(JOHN_DOE.getId());
        assertThat(user.getName()).isEqualTo(JOHN_DOE.getName());
        assertThat(user.getEmail()).isEqualTo(JOHN_DOE.getEmail());
    }

    @Test
    public void userShouldBeFoundByEmailWhenCreated() {
        User user = usersRepository.findByEmail(JOHN_DOE.getEmail()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(JOHN_DOE.getEmail());
        assertThat(user.getName()).isEqualTo(JOHN_DOE.getName());
    }

    @Test
    public void userShouldBeUpdatedSuccessfully() {
        User updatedJohnDoe = new User(999, "Johnathan Doe", "john.doe@example.com", "newhashedpassword");
        usersRepository.update(JOHN_DOE.getId(), updatedJohnDoe);

        User user = usersRepository.findById(JOHN_DOE.getId()).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo(updatedJohnDoe.getName());
        assertThat(user.getPasswordHash()).isEqualTo(updatedJohnDoe.getPasswordHash());
    }

    @Test
    public void userShouldNotBeFoundAfterDeletion() {
        usersRepository.delete(JOHN_DOE.getId());

        User user = usersRepository.findById(JOHN_DOE.getId()).orElse(null);
        assertThat(user).isNull();
    }

    @Test
    public void findAllShouldReturnAllUsers() {
        User janeDoe = new User(1000, "Jane Doe", "jane.doe@example.com", "hashedpassword");
        usersRepository.saveById(janeDoe);

        List<User> users = usersRepository.findAll();

        assertThat(users).hasSizeGreaterThanOrEqualTo(2);
        assertThat(users).contains(JOHN_DOE, janeDoe);

        usersRepository.delete(janeDoe.getId());
    }
}
