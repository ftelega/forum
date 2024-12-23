package ft.projects.forum.model;

import java.util.UUID;

public record ForumCommentRequest(
        UUID threadId,
        String content
) {
}
