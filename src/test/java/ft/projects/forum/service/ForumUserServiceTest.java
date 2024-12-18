package ft.projects.forum.service;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.model.ForumUser;
import ft.projects.forum.model.ForumUserRequest;
import ft.projects.forum.repository.ForumUserRepository;
import ft.projects.forum.security.service.JwtService;
import ft.projects.forum.security.service.SecurityContextService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.format.DateTimeFormatter;
import java.util.Date;
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
    private final DateTimeFormatter formatter = mock(DateTimeFormatter.class);
    private final ForumUserService userService = new ForumUserServiceImpl(userRepository, passwordEncoder, contextService, jwtService, formatter);

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
        given(jwtService.getDate(any())).willReturn(new Date());
        var res = userService.login();
        verify(contextService, times(1)).getUserFromContext();
        verify(jwtService, times(1)).getToken(user.getUsername());
        verify(jwtService, times(1)).getDate(any());
        verify(formatter, times(1)).format(any());
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

    @Test
    public void givenValidUsername_whenUpdateUsername_thenVerifyCalls() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.empty());
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        userService.updateUsername(TEST_USERNAME);
        verify(userRepository, times(1)).findByUsername(TEST_USERNAME);
        verify(contextService, times(1)).getUserFromContext();
        verify(userRepository, times(1)).save(any());
        verify(jwtService, times(1)).invalidate();
    }

    @Test
    public void givenUsernameExists_whenUpdateUsername_thenThrow() {
        given(userRepository.findByUsername(TEST_USERNAME)).willReturn(Optional.of(new ForumUser()));
        assertThrows(ForumException.class, () -> {
            userService.updateUsername(TEST_USERNAME);
        });
    }

    @Test
    public void givenInvalidUsername_whenUpdateUsername_thenThrow() {
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
           userService.updateUsername(null);
        });
    }

    @Test
    public void givenInvalidUsername_whenUpdateUsername_thenThrow2() {
        given(userRepository.findByUsername(any())).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            userService.updateUsername("");
        });
    }

    @Test
    public void givenValidPassword_whenUpdatePassword_thenVerifyCalls() {
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        userService.updatePassword(TEST_PASSWORD);
        verify(contextService, times(1)).getUserFromContext();
        verify(passwordEncoder, times(1)).encode(TEST_PASSWORD);
        verify(userRepository, times(1)).save(any());
        verify(jwtService, times(1)).invalidate();
    }

    @Test
    public void givenInvalidPassword_whenUpdatePassword_thenThrow() {
        assertThrows(ForumException.class, () -> {
            userService.updatePassword(null);
        });
    }

    @Test
    public void givenInvalidPassword_whenUpdatePassword_thenThrow2() {
        assertThrows(ForumException.class, () -> {
            userService.updatePassword("");
        });
    }

    @Test
    public void whenDelete_thenVerifyCalls() {
        userService.delete();
        verify(contextService, times(1)).getUserFromContext();
        verify(userRepository, times(1)).delete(any());
        verify(jwtService, times(1)).invalidate();
    }
}