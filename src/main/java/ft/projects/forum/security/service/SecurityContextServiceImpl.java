package ft.projects.forum.security.service;

import ft.projects.forum.model.ForumUser;
import ft.projects.forum.security.model.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextServiceImpl implements SecurityContextService {

    @Override
    public ForumUser getUserFromContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getCurrentUser();
        }
        throw new IllegalStateException();
    }
}
