package ft.projects.forum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ft.projects.forum.exception.ForumException;
import ft.projects.forum.exception.ForumExceptions;
import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.security.filter.JwtFilter;
import ft.projects.forum.service.ForumThreadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.mockito.BDDMockito.*;

@WebMvcTest(ForumThreadController.class)
@AutoConfigureMockMvc(addFilters = false)
class ForumThreadControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private ForumThreadService threadService;
    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    public ForumThreadControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void givenServiceNotThrow_whenCreateThread_thenStatusNoContent() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.post("/api/threads/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForumThreadRequest(null, null))));
        res.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void givenServiceThrows_whenCreateThread_thenStatusBadRequest() throws Exception {
        willThrow(new ForumException(ForumExceptions.INVALID_TITLE)).given(threadService).createThread(any());
        var res = mockMvc.perform(MockMvcRequestBuilders.post("/api/threads/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForumThreadRequest(null, null))));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenServiceNotThrow_whenGetThreads_thenStatusOk() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.get("/api/threads"));
        res.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void givenServiceThrows_whenGetThreads_thenStatusBadRequest() throws Exception {
        given(threadService.getThreads(anyInt(), anyInt(), anyBoolean())).willThrow(new ForumException(ForumExceptions.INVALID_PAGE));
        var res = mockMvc.perform(MockMvcRequestBuilders.get("/api/threads"));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenServiceNotThrow_whenUpdateContent_thenStatusNoContent() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.put("/api/threads/content")
                .param("id", UUID.randomUUID().toString())
                .param("content", ""));
        res.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void givenServiceThrows_whenUpdateContent_thenStatusBadRequest() throws Exception {
        willThrow(new ForumException(ForumExceptions.INVALID_CONTENT)).given(threadService).updateContent(any(), any());
        var res = mockMvc.perform(MockMvcRequestBuilders.put("/api/threads/content")
                .param("id", UUID.randomUUID().toString())
                .param("content", ""));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenServiceNotThrow_whenUpdateClosed_thenStatusNoContent() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.put("/api/threads/closed")
                .param("id", UUID.randomUUID().toString())
                .param("closed", "true"));
        res.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void givenServiceThrows_whenUpdateClosed_thenStatusBadRequest() throws Exception {
        willThrow(new ForumException(ForumExceptions.INVALID_ID)).given(threadService).updateClosed(any(), anyBoolean());
        var res = mockMvc.perform(MockMvcRequestBuilders.put("/api/threads/closed")
                .param("id", UUID.randomUUID().toString())
                .param("closed", "true"));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenServiceNotThrow_whenDeleteThread_thenStatusNoContent() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.delete("/api/threads/delete")
                .param("id", UUID.randomUUID().toString()));
        res.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void givenServiceThrows_whenDeleteThread_thenStatusBadRequest() throws Exception {
        willThrow(new ForumException(ForumExceptions.INVALID_ID)).given(threadService).deleteThread(any());
       var res = mockMvc.perform(MockMvcRequestBuilders.delete("/api/threads/delete")
                .param("id", UUID.randomUUID().toString()));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}