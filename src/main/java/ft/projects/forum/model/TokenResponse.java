package ft.projects.forum.model;

import java.time.ZonedDateTime;

public record TokenResponse(
        String token,
        ZonedDateTime expiration
) {
}
