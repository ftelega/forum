package ft.projects.forum.service;

import ft.projects.forum.model.ForumCommentResponse;
import ft.projects.forum.model.ForumCommentRequest;

import java.util.List;
import java.util.UUID;

public interface ForumCommentService {

    void createComment(ForumCommentRequest commentRequest);
    List<ForumCommentResponse> getCommentsForThread(UUID threadId);
    void updateContent(UUID uuid, String content);
    void deleteComment(UUID uuid);
}
