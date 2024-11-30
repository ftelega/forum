package ft.projects.forum;

import ft.projects.forum.model.ForumRole;
import ft.projects.forum.model.ForumUser;
import ft.projects.forum.repository.ForumUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminCommandLineRunner implements CommandLineRunner {

    private final ForumUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        var adminUsername = "admin";
        if(userRepository.findByUsername(adminUsername).isPresent()) return;
        userRepository.save(ForumUser.builder()
                .username(adminUsername)
                .password(passwordEncoder.encode(adminUsername))
                .timezone("Europe/Warsaw")
                .role(ForumRole.ROLE_ADMIN)
                .build()
        );
    }
}
