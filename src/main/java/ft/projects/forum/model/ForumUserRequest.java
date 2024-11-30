package ft.projects.forum.model;

public record ForumUserRequest(
        String username,
        String password,
        String timezone
) {
}
