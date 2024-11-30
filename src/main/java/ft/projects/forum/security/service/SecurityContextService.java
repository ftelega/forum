package ft.projects.forum.security.service;

import ft.projects.forum.model.ForumUser;

public interface SecurityContextService {

    ForumUser getUserFromContext();
}
