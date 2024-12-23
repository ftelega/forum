package ft.projects.forum.service;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.exception.ForumExceptions;
import ft.projects.forum.model.ForumThread;
import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumThreadResponse;
import ft.projects.forum.repository.ForumThreadRepository;
import ft.projects.forum.security.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForumThreadServiceImpl implements ForumThreadService {

    private final ForumThreadRepository threadRepository;
    private final SecurityContextService contextService;
    private final DateTimeFormatter formatter;

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
    public List<ForumThreadResponse> getThreads(int page, int size, boolean descending) {
        validatePage(page);
        validateSize(size);
        var timezone = contextService.getUserFromContext().getTimezone();
        var sort = descending ? Sort.by("publishedAt").descending() : Sort.by("publishedAt");
        return threadRepository.findAll(PageRequest.of(page, size, sort))
                .stream()
                .map(t -> new ForumThreadResponse(
                        t.getUuid(),
                        t.getUser().getUsername(),
                        t.getTitle(),
                        t.getContent(),
                        formatter.format(ZonedDateTime.ofInstant(t.getPublishedAt(), ZoneId.of(timezone)))
                    )
                )
                .toList();
    }

    @Override
    public void updateContent(UUID uuid, String content) {
        var thread = getThreadIfExistsAndValidOwnership(uuid);
        validateContent(content);
        thread.setContent(content);
        threadRepository.save(thread);
    }

    @Override
    public void updateClosed(UUID uuid, boolean closed) {
        var thread = getThreadIfExistsAndValidOwnership(uuid);
        thread.setClosed(closed);
        threadRepository.save(thread);
    }

    @Override
    public void deleteThread(UUID uuid) {
        var thread = getThreadIfExistsAndValidOwnership(uuid);
        threadRepository.delete(thread);
    }

    @Override
    public ForumThread getThread(UUID uuid) {
        return threadRepository.findById(uuid)
                .orElseThrow(() -> new ForumException(ForumExceptions.INVALID_ID));
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
        if(page < 0) throw new ForumException(ForumExceptions.INVALID_PAGE);
    }

    private void validateSize(int size) {
        if(size < 1) throw new ForumException(ForumExceptions.INVALID_SIZE);
    }

    private ForumThread getThreadIfExistsAndValidOwnership(UUID uuid) {
        var threadOptional = threadRepository.findById(uuid);
        if(threadOptional.isEmpty()) throw new ForumException(ForumExceptions.INVALID_ID);
        var thread = threadOptional.get();
        if(!thread.getUser().getUsername().equals(contextService.getUserFromContext().getUsername())) throw new ForumException(ForumExceptions.INVALID_OWNER);
        return thread;
    }
}
