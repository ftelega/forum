package ft.projects.forum.security.service;

public interface JwtService {

    String getToken(String username);
    String getUsername(String token);
    void invalidate();
    boolean isInvalidated(String jwt);
    void setToken(String jwt);
    void clearToken();
}
