package ft.projects.forum.service;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.model.ForumThread;
import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumUser;
import ft.projects.forum.repository.ForumThreadRepository;
import ft.projects.forum.security.service.SecurityContextService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static ft.projects.forum.Constants.*;

class ForumThreadServiceTest {

    private final ForumThreadRepository threadRepository = mock(ForumThreadRepository.class);
    private final SecurityContextService contextService = mock(SecurityContextService.class);
    private final DateTimeFormatter formatter = mock(DateTimeFormatter.class);
    private final ForumThreadService threadService = new ForumThreadServiceImpl(threadRepository, contextService, formatter);

    @Test
    public void givenValidForumThreadRequestObject_whenCreateForumThread_thenVerifyCalls() {
        threadService.createThread(new ForumThreadRequest(TEST_THREAD_TITLE, TEST_THREAD_CONTENT));
        verify(contextService, times(1)).getUserFromContext();
        verify(threadRepository, times(1)).save(any());
    }

    @Test
    public void givenInvalidTitle_whenCreateForumThread_thenThrow() {
        assertThrows(ForumException.class, () -> {
           threadService.createThread(new ForumThreadRequest(null, TEST_THREAD_CONTENT));
        });
    }

    @Test
    public void givenInvalidTitle_whenCreateForumThread_thenThrow2() {
        assertThrows(ForumException.class, () -> {
            threadService.createThread(new ForumThreadRequest("", TEST_THREAD_CONTENT));
        });
    }

    @Test
    public void givenInvalidContent_whenCreateForumThread_thenThrow() {
        assertThrows(ForumException.class, () -> {
            threadService.createThread(new ForumThreadRequest(TEST_THREAD_TITLE, null));
        });
    }

    @Test
    public void givenInvalidContent_whenCreateForumThread_thenThrow2() {
        assertThrows(ForumException.class, () -> {
            threadService.createThread(new ForumThreadRequest(TEST_THREAD_TITLE, ""));
        });
    }

    @Test
    public void givenValidRequest_whenGetThreads_thenVerifyCalls() {
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        given(threadRepository.findAll((Pageable) any())).willReturn(Page.empty());
        threadService.getThreads(1, 1, false);
        verify(contextService, times(1)).getUserFromContext();
        verify(threadRepository, times(1)).findAll(PageRequest.of(1, 1, Sort.by("publishedAt")));
    }

    @Test
    public void givenValidRequestDescending_whenGetThreads_thenVerifyCalls() {
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        given(threadRepository.findAll((Pageable) any())).willReturn(Page.empty());
        threadService.getThreads(1, 1, true);
        verify(contextService, times(1)).getUserFromContext();
        verify(threadRepository, times(1)).findAll(PageRequest.of(1, 1, Sort.by("publishedAt").descending()));
    }

    @Test
    public void givenInvalidPage_whenGetThreads_thenThrow() {
        assertThrows(ForumException.class, () -> {
           threadService.getThreads(-1, 1, false);
        });
    }

    @Test
    public void givenInvalidSize_whenGetThreads_thenThrow() {
        assertThrows(ForumException.class, () -> {
            threadService.getThreads(1, -1, false);
        });
    }

    @Test
    public void givenInvalidSize_whenGetThreads_thenThrow2() {
        assertThrows(ForumException.class, () -> {
            threadService.getThreads(1, 0, false);
        });
    }

    @Test
    public void givenValidRequest_whenUpdateContent_thenVerifyCalls() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var thread = ForumThread.builder()
                .user(user)
                .build();
        given(threadRepository.findById(any())).willReturn(Optional.of(thread));
        given(contextService.getUserFromContext()).willReturn(user);
        threadService.updateContent(UUID.randomUUID(), TEST_THREAD_CONTENT);
        verify(threadRepository, times(1)).findById(any());
        verify(contextService, times(1)).getUserFromContext();
        verify(threadRepository, times(1)).save(any());
    }

    @Test
    public void givenInvalidId_whenUpdateContent_thenThrow() {
        given(threadRepository.findById(any())).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            threadService.updateContent(UUID.randomUUID(), TEST_THREAD_CONTENT);
        });
    }

    @Test
    public void givenInvalidOwner_whenUpdateContent_thenThrow() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var thread = ForumThread.builder()
                .user(user)
                .build();
        given(threadRepository.findById(any())).willReturn(Optional.of(thread));
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        assertThrows(ForumException.class, () -> {
            threadService.updateContent(UUID.randomUUID(), TEST_THREAD_CONTENT);
        });
    }

    @Test
    public void givenInvalidContent_whenUpdateContent_thenThrow() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var thread = ForumThread.builder()
                .user(user)
                .build();
        given(threadRepository.findById(any())).willReturn(Optional.of(thread));
        given(contextService.getUserFromContext()).willReturn(user);
        assertThrows(ForumException.class, () -> {
            threadService.updateContent(UUID.randomUUID(), null);
        });
    }

    @Test
    public void givenInvalidContent_whenUpdateContent_thenThrow2() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var thread = ForumThread.builder()
                .user(user)
                .build();
        given(threadRepository.findById(any())).willReturn(Optional.of(thread));
        given(contextService.getUserFromContext()).willReturn(user);
        assertThrows(ForumException.class, () -> {
            threadService.updateContent(UUID.randomUUID(), "");
        });
    }

    @Test
    public void givenValidRequest_whenUpdateClosed_thenVerifyCalls() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var thread = ForumThread.builder()
                .user(user)
                .build();
        given(threadRepository.findById(any())).willReturn(Optional.of(thread));
        given(contextService.getUserFromContext()).willReturn(user);
        threadService.updateClosed(UUID.randomUUID(), true);
        verify(threadRepository, times(1)).findById(any());
        verify(contextService, times(1)).getUserFromContext();
        verify(threadRepository, times(1)).save(any());
    }

    @Test
    public void givenInvalidId_whenUpdateClosed_thenThrow() {
        given(threadRepository.findById(any())).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            threadService.updateClosed(UUID.randomUUID(), true);
        });
    }

    @Test
    public void givenInvalidOwner_whenUpdateClosed_thenThrow() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var thread = ForumThread.builder()
                .user(user)
                .build();
        given(threadRepository.findById(any())).willReturn(Optional.of(thread));
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        assertThrows(ForumException.class, () -> {
            threadService.updateClosed(UUID.randomUUID(), true);
        });
    }

    @Test
    public void givenValidRequest_whenDeleteThread_thenVerifyCalls() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var thread = ForumThread.builder()
                .user(user)
                .build();
        given(threadRepository.findById(any())).willReturn(Optional.of(thread));
        given(contextService.getUserFromContext()).willReturn(user);
        threadService.deleteThread(UUID.randomUUID());
        verify(threadRepository, times(1)).findById(any());
        verify(contextService, times(1)).getUserFromContext();
        verify(threadRepository, times(1)).delete(any());
    }

    @Test
    public void givenInvalidId_whenDeleteThread_thenThrow() {
        given(threadRepository.findById(any())).willReturn(Optional.empty());
        assertThrows(ForumException.class, () -> {
            threadService.deleteThread(UUID.randomUUID());
        });
    }

    @Test
    public void givenInvalidOwner_whenDeleteThread_thenThrow() {
        var user = ForumUser.builder()
                .username(TEST_USERNAME)
                .build();
        var thread = ForumThread.builder()
                .user(user)
                .build();
        given(threadRepository.findById(any())).willReturn(Optional.of(thread));
        given(contextService.getUserFromContext()).willReturn(new ForumUser());
        assertThrows(ForumException.class, () -> {
            threadService.deleteThread(UUID.randomUUID());
        });
    }
}