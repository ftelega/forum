package ft.projects.forum.service;

import ft.projects.forum.exception.ForumException;
import ft.projects.forum.exception.ForumExceptions;
import ft.projects.forum.model.ForumComment;
import ft.projects.forum.model.ForumCommentResponse;
import ft.projects.forum.model.ForumCommentRequest;
import ft.projects.forum.repository.ForumCommentRepository;
import ft.projects.forum.security.service.SecurityContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForumCommentServiceImpl implements ForumCommentService {

    private final ForumCommentRepository commentRepository;
    private final SecurityContextService contextService;
    private final ForumThreadService threadService;
    private final DateTimeFormatter formatter;

    @Override
    public void createComment(ForumCommentRequest commentRequest) {
        var thread = threadService.getThread(commentRequest.threadId());
        if(thread.isClosed()) throw new ForumException(ForumExceptions.THREAD_CLOSED);
        validateContent(commentRequest.content());
        commentRepository.save(ForumComment.builder()
                .content(commentRequest.content())
                .publishedAt(Instant.now())
                .user(contextService.getUserFromContext())
                .thread(thread)
                .build()
        );
    }

    @Override
    public List<ForumCommentResponse> getCommentsForThread(UUID threadId) {
        var thread = threadService.getThread(threadId);
        var timezone = contextService.getUserFromContext().getTimezone();
        return thread.getComments().stream()
                .map(c -> new ForumCommentResponse(
                        c.getUuid(),
                        c.getContent(),
                        formatter.format(ZonedDateTime.ofInstant(c.getPublishedAt(), ZoneId.of(timezone))))
                )
                .toList();
    }

    @Override
    public void updateContent(UUID uuid, String content) {
        var comment = getCommentIfExistsAndValidOwnership(uuid);
        validateContent(content);
        comment.setContent(content);
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(UUID uuid) {
        var comment = getCommentIfExistsAndValidOwnership(uuid);
        commentRepository.delete(comment);
    }

    private void validateContent(String content) {
        if(content == null || content.length() < 5) {
            throw new ForumException(ForumExceptions.INVALID_CONTENT);
        }
    }

    private ForumComment getCommentIfExistsAndValidOwnership(UUID uuid) {
        var commentOptional = commentRepository.findById(uuid);
        if(commentOptional.isEmpty()) throw new ForumException(ForumExceptions.INVALID_ID);
        var comment = commentOptional.get();
        if(!comment.getUser().getUsername().equals(contextService.getUserFromContext().getUsername())) throw new ForumException(ForumExceptions.INVALID_OWNER);
        return comment;
    }
}
