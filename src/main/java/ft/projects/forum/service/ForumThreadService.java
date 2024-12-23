package ft.projects.forum.service;

import ft.projects.forum.model.ForumThread;
import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumThreadResponse;

import java.util.List;
import java.util.UUID;

public interface ForumThreadService {

    void createThread(ForumThreadRequest threadRequest);
    List<ForumThreadResponse> getThreads(int page, int size, boolean descending);
    void updateContent(UUID uuid, String content);
    void updateClosed(UUID uuid, boolean closed);
    void deleteThread(UUID uuid);
    ForumThread getThread(UUID uuid);
}
