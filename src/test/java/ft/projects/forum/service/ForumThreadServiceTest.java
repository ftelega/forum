package ft.projects.forum.service;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumUser;
import ft.projects.forum.repository.ForumThreadRepository;
import ft.projects.forum.security.service.SecurityContextService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static ft.projects.forum.Constants.*;

class ForumThreadServiceTest {

    private final ForumThreadRepository threadRepository = mock(ForumThreadRepository.class);
    private final SecurityContextService contextService = mock(SecurityContextService.class);
    private final ForumThreadService threadService = new ForumThreadServiceImpl(threadRepository, contextService);

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
}