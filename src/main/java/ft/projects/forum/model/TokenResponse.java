package ft.projects.forum.model;

public record TokenResponse(
        String token,
        String expiration
) {
}
