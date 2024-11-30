package ft.projects.forum.service;

import ft.projects.forum.model.ForumUserRequest;
import ft.projects.forum.model.TokenResponse;

public interface ForumUserService {

    void register(ForumUserRequest userRequest);
    TokenResponse login();
}
