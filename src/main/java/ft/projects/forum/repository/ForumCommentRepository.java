package ft.projects.forum.repository;

import ft.projects.forum.model.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ForumCommentRepository extends JpaRepository<ForumComment, UUID> {

}
