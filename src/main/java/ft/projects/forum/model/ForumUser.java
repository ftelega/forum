package ft.projects.forum.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForumUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;
    private String username;
    private String password;
    private String timezone;
    @Enumerated(value = EnumType.STRING)
    @Column(length = 32)
    private ForumRole role;
    @OneToMany(mappedBy = "user")
    private List<ForumThread> threads;
    @OneToMany(mappedBy = "user")
    private List<ForumComment> comments;
}
