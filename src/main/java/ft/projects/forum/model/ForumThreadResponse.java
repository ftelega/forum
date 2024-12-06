package ft.projects.forum.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ForumThreadResponse(
        UUID uuid,
        String title,
        String content,
        ZonedDateTime publishedAt
) {
}
