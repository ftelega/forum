package ft.projects.forum.service;

import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumThreadResponse;

import java.util.List;

public interface ForumThreadService {

    void createThread(ForumThreadRequest threadRequest);
    List<ForumThreadResponse> getThreads(int page, int size, boolean descending);
}
