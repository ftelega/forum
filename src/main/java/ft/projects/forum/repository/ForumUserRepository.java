package ft.projects.forum.repository;

import ft.projects.forum.model.ForumUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ForumUserRepository extends JpaRepository<ForumUser, UUID> {

    Optional<ForumUser> findByUsername(String username);
}
