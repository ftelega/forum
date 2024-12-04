package ft.projects.forum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ft.projects.forum.exception.ForumException;
import ft.projects.forum.exception.ForumExceptions;
import ft.projects.forum.model.ForumUserRequest;
import ft.projects.forum.security.filter.JwtFilter;
import ft.projects.forum.service.ForumUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.*;

@WebMvcTest(ForumUserController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForumUserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private ForumUserService userService;
    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    public ForumUserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void givenServiceNotThrow_whenRegister_thenStatusNoContent() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForumUserRequest(null, null, null))));
        res.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void givenServiceThrows_whenRegister_thenStatusBadRequest() throws Exception {
        doThrow(new ForumException(ForumExceptions.INVALID_PASSWORD)).when(userService).register(any());
        var res = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForumUserRequest(null, null, null))));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void whenLogin_thenStatusOk() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.post("/api/users/login"));
        res.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void whenGetUsers_thenStatusOk() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"));
        res.andExpect(MockMvcResultMatchers.status().isOk());
    }
}