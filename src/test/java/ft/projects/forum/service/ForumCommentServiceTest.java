package ft.projects.forum.service;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.exception.ForumExceptions;
import ft.projects.forum.model.ForumComment;
import ft.projects.forum.model.ForumCommentRequest;
import ft.projects.forum.model.ForumThread;
import ft.projects.forum.model.ForumUser;
import ft.projects.forum.repository.ForumCommentRepository;
import ft.projects.forum.security.service.SecurityContextService;
import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ft.projects.forum.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class ForumCommentServiceTest {

    private final ForumCommentRepository commentRepository = mock(ForumCommentRepository.class);
    private final SecurityContextService contextService = mock(SecurityContextService.class);
    private final ForumThreadService threadService = mock(ForumThreadService.class);
    private final DateTimeFormatter formatter = mock(DateTimeFormatter.class);
    private final ForumCommentService commentService = new ForumCommentServiceImpl(commentRepository, contextService, threadService, formatter);

    @Test
    public void givenValidRequest_whenCreateComment_thenVerifyCalls() {
        given(threadService.getThread(any())).willReturn(new ForumThread());
        commentService.createComment(new ForumCommentRequest(UUID.randomUUID(), TEST_COMMENT_CONTENT));
        verify(threadService, times(1)).getThread(any());
        verify(contextService, times(1)).getUserFromContext();
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    public void givenThreadClosed_whenCreateComment_thenThrow() {
        given(threadService.getThread(any())).willReturn(ForumThread.builder().isClosed(true).build());
        assertThrows(ForumException.class, () -> {
             commentService.createComment(new ForumCommentRequest(UUID.randomUUID(), TEST_COMMENT_CONTENT));
        });
    }

    @Test
    public void givenInvalidId_whenCreateComment_thenThrow() {
        given(threadService.getThread(any())).willThrow(new ForumException(ForumExceptions.INVALID_ID));
        assertThrows(ForumException.class, () -> {
            commentService.createComment(new ForumCommentRequest(UUID.randomUUID(), TEST_COMMENT_CONTENT));
        });
    }

    @Test
    public void givenInvalidContent_whenCreateComment_thenThrow() {
        given(threadService.getThread(any())).willReturn(new ForumThread());
        assertThrows(ForumException.class, () -> {
            commentService.createComment(new ForumCommentRequest(UUID.randomUUID(), null));
        });
    }

    @Test
    public void givenInvalidContent_whenCreateComment_thenThrow2() {
        given(threadService.getThread(any())).willReturn(new ForumThread());
        assertThrows(ForumException.class, () -> {
            commentService.createComment(new ForumCommentRequest(UUID.randomUUID(), ""));
        });
    }

    @Test
    public void givenValidId_whenGetCommentsForThread_thenVerifyCalls() {
        given(threadService.getThread(any())).willReturn(ForumThread.builder().comments(List.of()).build());
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        var res = commentService.getCommentsForThread(UUID.randomUUID());
        verify(threadService, times(1)).getThread(any());
        verify(contextService, times(1)).getUserFromContext();
        assertNotNull(res);
    }

    @Test
    public void givenInvalidId_whenGetCommentsForThread_thenThrow() {
        given(threadService.getThread(any())).willThrow(new ForumException(ForumExceptions.INVALID_ID));
        assertThrows(ForumException.class, () -> {
            commentService.getCommentsForThread(UUID.randomUUID());
        });
    }

    @Test
    public void givenValidRequest_whenUpdateContent_thenVerifyCalls() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var comment = ForumComment.builder()
                .user(user)
                .build();
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));
        given(contextService.getUserFromContext()).willReturn(user);
        commentService.updateContent(UUID.randomUUID(), TEST_COMMENT_CONTENT);
        verify(commentRepository, times(1)).findById(any());
        verify(contextService, times(1)).getUserFromContext();
        verify(commentRepository, times(1)).save(any());
    }

    @Test
    public void givenInvalidOwner_whenUpdateContent_thenThrow() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var comment = ForumComment.builder()
                .user(user)
                .build();
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        assertThrows(ForumException.class, () -> {
            commentService.updateContent(UUID.randomUUID(), TEST_COMMENT_CONTENT);
        });
    }

    @Test
    public void givenInvalidId_whenUpdateContent_thenThrow() {
        given(commentRepository.findById(any())).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            commentService.updateContent(UUID.randomUUID(), TEST_COMMENT_CONTENT);
        });
    }

    @Test
    public void givenInvalidContent_whenUpdateContent_thenThrow() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var comment = ForumComment.builder()
                .user(user)
                .build();
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));
        given(contextService.getUserFromContext()).willReturn(user);
        assertThrows(ForumException.class, () -> {
            commentService.updateContent(UUID.randomUUID(), null);
        });
    }

    @Test
    public void givenInvalidContent_whenUpdateContent_thenThrow2() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var comment = ForumComment.builder()
                .user(user)
                .build();
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));
        given(contextService.getUserFromContext()).willReturn(user);
        assertThrows(ForumException.class, () -> {
            commentService.updateContent(UUID.randomUUID(), "");
        });
    }

    @Test
    public void givenValidRequest_whenDeleteComment_thenVerifyCalls() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var comment = ForumComment.builder()
                .user(user)
                .build();
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));
        given(contextService.getUserFromContext()).willReturn(user);
        commentService.deleteComment(UUID.randomUUID());
        verify(commentRepository, times(1)).findById(any());
        verify(contextService, times(1)).getUserFromContext();
        verify(commentRepository, times(1)).delete(any());
    }

    @Test
    public void givenInvalidOwner_whenDeleteComment_thenThrow() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var comment = ForumComment.builder()
                .user(user)
                .build();
        given(commentRepository.findById(any())).willReturn(Optional.of(comment));
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        assertThrows(ForumException.class, () -> {
            commentService.deleteComment(UUID.randomUUID());
        });
    }

    @Test
    public void givenInvalidId_whenDeleteComment_thenThrow() {
        given(commentRepository.findById(any())).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            commentService.deleteComment(UUID.randomUUID());
        });
    }
}