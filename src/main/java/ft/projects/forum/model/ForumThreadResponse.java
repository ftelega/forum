package ft.projects.forum.model;

import java.util.UUID;

public record ForumThreadResponse(
        UUID uuid,
        String creator,
        String title,
        String content,
        String publishedAt
) {
}
