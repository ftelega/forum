package ft.projects.forum.service;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.exception.ForumExceptions;
import ft.projects.forum.model.ForumThread;
import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumThreadResponse;
import ft.projects.forum.repository.ForumThreadRepository;
import ft.projects.forum.security.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class ForumThreadServiceImpl implements ForumThreadService {

    private final ForumThreadRepository threadRepository;
    private final SecurityContextService contextService;

    @Override
    public void createThread(ForumThreadRequest threadRequest) {
        validateTitle(threadRequest.title());
        validateContent(threadRequest.content());
        threadRepository.save(ForumThread.builder()
                .title(threadRequest.title())
                .content(threadRequest.content())
                .user(contextService.getUserFromContext())
                .isClosed(false)
                .publishedAt(Instant.now())
                .build()
        );
    }

    @Override
    public Page<ForumThreadResponse> getThreads(int page, int size, String sort) {
        validatePage(page);
        validateSize(size);
        validateSort(sort);
        var timezone = contextService.getUserFromContext().getTimezone();
        return threadRepository.findAll(PageRequest.of(page, size, Sort.by(sort)))
                .map(t -> new ForumThreadResponse(
                        t.getUuid(),
                        t.getTitle(),
                        t.getContent(),
                        ZonedDateTime.ofInstant(t.getPublishedAt(), ZoneId.of(timezone))
                    )
                );
    }

    private void validateTitle(String title) {
        if(title == null || title.length() < 5) {
            throw new ForumException(ForumExceptions.INVALID_TITLE);
        }
    }

    private void validateContent(String content) {
        if(content == null || content.length() < 5) {
            throw new ForumException(ForumExceptions.INVALID_CONTENT);
        }
    }

    private void validatePage(int page) {
        if(page <= 0) throw new ForumException(ForumExceptions.INVALID_PAGE);
    }

    private void validateSize(int size) {
        if(size <= 0) throw new ForumException(ForumExceptions.INVALID_SIZE);
    }

    private void validateSort(String sort) {
        if(sort == null || !(sort.equals("publishedAt") || sort.equals("title") || sort.equals("content"))) {
            throw new ForumException(ForumExceptions.INVALID_SORT);
        }
    }
}
