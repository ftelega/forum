package ft.projects.forum.exception;

public class ForumException extends RuntimeException {

    public ForumException(ForumExceptions message) {
        super(message.name());
    }
}
