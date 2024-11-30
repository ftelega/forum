package ft.projects.forum.security.service;

import ft.projects.forum.model.ForumUser;
import ft.projects.forum.security.model.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityContextServiceTest {

    private final SecurityContextService contextService = new SecurityContextServiceImpl();

    @Test
    public void givenValidAuthAndPrincipal_whenGetUserFromContext_thenUserReturned() {
        var user = new ForumUser();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                new UserDetailsImpl(user), null, List.of()
        ));
        var res = contextService.getUserFromContext();
        assertNotNull(res);
        assertEquals(user, res);
    }

    @Test
    public void givenNoAuth_whenGetUserFromContext_thenThrow() {
        assertThrows(IllegalStateException.class, () -> {
            contextService.getUserFromContext();
        });
    }

    @Test
    public void givenInvalidPrincipal_whenGetUserFromContext_thenThrow() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "invalid-principal", null, List.of()
        ));
        assertThrows(IllegalStateException.class, () -> {
            contextService.getUserFromContext();
        });
    }
}