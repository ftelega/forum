package ft.projects.forum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForumComment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    private String content;
    private Instant publishedAt;
    @ManyToOne
    private ForumUser user;
    @ManyToOne
    private ForumThread thread;
}
