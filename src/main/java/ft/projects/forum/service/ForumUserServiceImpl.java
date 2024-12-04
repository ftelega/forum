package ft.projects.forum.service;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.exception.ForumExceptions;
import ft.projects.forum.model.*;
import ft.projects.forum.repository.ForumUserRepository;
import ft.projects.forum.security.service.JwtService;
import ft.projects.forum.security.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumUserServiceImpl implements ForumUserService {

    private final ForumUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextService contextService;
    private final JwtService jwtService;
    @Value("${token-expiration}")
    private Long tokenExpiration;

    @Override
    public void register(ForumUserRequest userRequest) {
        validateUsername(userRequest.username());
        validatePassword(userRequest.password());
        validateTimezone(userRequest.timezone());
        userRepository.save(ForumUser.builder()
                .username(userRequest.username())
                .password(passwordEncoder.encode(userRequest.password()))
                .timezone(userRequest.timezone())
                .role(ForumRole.ROLE_USER)
                .build()
        );
    }

    @Override
    public TokenResponse login() {
        var user = contextService.getUserFromContext();
        return new TokenResponse(
                jwtService.getToken(user.getUsername()),
                ZonedDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis() + tokenExpiration), ZoneId.of(user.getTimezone()))
        );
    }

    @Override
    public List<ForumUserResponse> getUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> new ForumUserResponse(u.getUsername()))
                .toList();
    }

    private void validateUsername(String username) {
        if(username == null || username.length() < 5) {
            throw new ForumException(ForumExceptions.INVALID_USERNAME);
        } else if(userRepository.findByUsername(username).isPresent()) {
            throw new ForumException(ForumExceptions.USER_EXISTS);
        }
    }

    private void validatePassword(String password) {
        if(password == null || password.length() < 8) {
            throw new ForumException(ForumExceptions.INVALID_PASSWORD);
        }
    }

    private void validateTimezone(String timezone) {
        try {
            if(timezone == null) throw new DateTimeException(null);
            ZoneId.of(timezone);
        } catch (DateTimeException e) {
            throw new ForumException(ForumExceptions.INVALID_TIMEZONE);
        }
    }
}
