package ft.projects.forum.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "threads")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForumThread {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    private String title;
    private String content;
    private Instant publishedAt;
    private boolean isClosed;
    @ManyToOne
    private ForumUser user;
    @OneToMany(mappedBy = "thread", cascade = CascadeType.REMOVE)
    private List<ForumComment> comments;
}
