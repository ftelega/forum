package ft.projects.forum.model;

import java.util.UUID;

public record ForumCommentResponse(
        UUID uuid,
        String content,
        String publishedAt
) {
}
