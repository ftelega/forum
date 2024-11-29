package ft.projects.forum.repository;

import ft.projects.forum.model.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ForumThreadRepository extends JpaRepository<ForumThread, UUID> {

}
