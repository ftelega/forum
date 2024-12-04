package ft.projects.forum.service;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.model.ForumUser;
import ft.projects.forum.model.ForumUserRequest;
import ft.projects.forum.repository.ForumUserRepository;
import ft.projects.forum.security.service.JwtService;
import ft.projects.forum.security.service.SecurityContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static ft.projects.forum.Constants.*;

class ForumUserServiceTest {

    private final ForumUserRepository userRepository = mock(ForumUserRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final SecurityContextService contextService = mock(SecurityContextService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final ForumUserService userService = new ForumUserServiceImpl(userRepository, passwordEncoder, contextService, jwtService);

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(userService, "tokenExpiration", 3_600_000L);
    }

    @Test
    public void givenValidRequest_whenRegister_thenVerifyCalls() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.empty());
        userService.register(new ForumUserRequest(TEST_USERNAME, TEST_PASSWORD, TEST_TIMEZONE));
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(passwordEncoder, times(1)).encode(TEST_PASSWORD);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    public void givenUserExists_whenRegister_thenThrow() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.of(new ForumUser()));
        assertThrows(ForumException.class, () -> {
           userService.register(new ForumUserRequest(TEST_USERNAME, TEST_PASSWORD, TEST_TIMEZONE));
        });
    }

    @Test
    public void givenInvalidUsername_whenRegister_thenThrow() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            userService.register(new ForumUserRequest(null, TEST_PASSWORD, TEST_TIMEZONE));
        });
    }

    @Test
    public void givenInvalidUsername2_whenRegister_thenThrow() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            userService.register(new ForumUserRequest("", TEST_PASSWORD, TEST_TIMEZONE));
        });
    }

    @Test
    public void givenInvalidPassword_whenRegister_thenThrow() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            userService.register(new ForumUserRequest(TEST_USERNAME, null, TEST_TIMEZONE));
        });
    }

    @Test
    public void givenInvalidPassword2_whenRegister_thenThrow() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            userService.register(new ForumUserRequest(TEST_USERNAME, "", TEST_TIMEZONE));
        });
    }

    @Test
    public void givenInvalidTimezone_whenRegister_thenThrow() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            userService.register(new ForumUserRequest(TEST_USERNAME, TEST_PASSWORD, null));
        });
    }

    @Test
    public void givenInvalidTimezone2_whenRegister_thenThrow() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            userService.register(new ForumUserRequest(TEST_USERNAME, TEST_PASSWORD, ""));
        });
    }

    @Test
    public void whenLogin_thenVerifyCalls() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .timezone(TEST_TIMEZONE)
                .build();
        given(contextService.getUserFromContext()).willReturn(user);
        var res = userService.login();
        verify(contextService, times(1)).getUserFromContext();
        verify(jwtService, times(1)).getToken(user.getUsername());
        assertNotNull(res);
    }

    @Test
    public void whenGetUsers_thenVerifyCalls() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .timezone(TEST_TIMEZONE)
                .build();
        given(userRepository.findAll()).willReturn(List.of(user));
        var res = userService.getUsers();
        verify(userRepository, times(1)).findAll();
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals(user.getUsername(), res.get(0).username());
    }
}