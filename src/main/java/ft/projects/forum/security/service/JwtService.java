package ft.projects.forum.security.service;

import java.util.Date;

public interface JwtService {

    String getToken(String username);
    String getUsername(String jwt);
    Date getDate(String jwt);
    void invalidate();
    boolean isInvalidated(String jwt);
    void setToken(String jwt);
    void clearToken();
}
