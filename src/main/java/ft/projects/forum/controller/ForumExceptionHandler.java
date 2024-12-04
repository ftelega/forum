package ft.projects.forum.controller;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.exception.ForumExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ForumExceptionHandler {

    @ExceptionHandler(value = ForumException.class)
    public ResponseEntity<ForumExceptionResponse> handleForumException(ForumException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ForumExceptionResponse(e.getMessage()));
    }
}
