package ft.projects.forum.service;

import ft.projects.forum.model.ForumUserRequest;
import ft.projects.forum.model.ForumUserResponse;
import ft.projects.forum.model.TokenResponse;

import java.util.List;

public interface ForumUserService {

    void register(ForumUserRequest userRequest);
    TokenResponse login();
    List<ForumUserResponse> getUsers();
    void updateUsername(String username);
    void updatePassword(String password);
    void delete();
}
