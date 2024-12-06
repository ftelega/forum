package ft.projects.forum.service;

import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumThreadResponse;
import org.springframework.data.domain.Page;

public interface ForumThreadService {

    void createThread(ForumThreadRequest threadRequest);
    Page<ForumThreadResponse> getThreads(int page, int size, String sort);
}
