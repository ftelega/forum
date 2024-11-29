package ft.projects.forum.repository;

import ft.projects.forum.AbstractIntegrationTest;
import ft.projects.forum.model.ForumUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;
import static ft.projects.forum.Constants.TEST_USERNAME;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ForumUserRepositoryTest extends AbstractIntegrationTest {

    private final ForumUserRepository userRepository;

    @Autowired
    public ForumUserRepositoryTest(ForumUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void clear() {
        userRepository.deleteAll();
    }

    @Test
    public void givenUserExists_whenFindByUsername_thenUserPresent() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        userRepository.save(user);
        var res = userRepository.findByUsername(user.getUsername());
        assertNotNull(res);
        assertTrue(res.isPresent());
        assertEquals(user.getUsername(), res.get().getUsername());
    }

    @Test
    public void givenUserNotExist_whenFindByUsername_thenUserAbsent() {
        var res = userRepository.findByUsername(TEST_USERNAME);
        assertNotNull(res);
        assertFalse(res.isPresent());
    }
}