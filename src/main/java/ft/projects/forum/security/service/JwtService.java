package ft.projects.forum.security.service;

public interface JwtService {

    String getToken(String username);
    String getUsername(String token);
}
