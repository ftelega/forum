package ft.projects.forum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ft.projects.forum.exception.ForumException;
import ft.projects.forum.exception.ForumExceptions;
import ft.projects.forum.model.ForumCommentRequest;
import ft.projects.forum.security.filter.JwtFilter;
import ft.projects.forum.service.ForumCommentService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

@WebMvcTest(ForumCommentController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ForumCommentControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    @MockBean
    private ForumCommentService commentService;
    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    public ForumCommentControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void givenServiceNotThrow_whenCreateComment_thenStatusNoContent() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForumCommentRequest(null, null))));
        res.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void givenServiceThrows_whenCreateComment_thenStatusBadRequest() throws Exception {
        willThrow(new ForumException(ForumExceptions.INVALID_CONTENT)).given(commentService).createComment(any());
        var res = mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ForumCommentRequest(null, null))));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenServiceNotThrow_whenGetCommentsForThread_thenStatusOk() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.get("/api/comments")
                .param("id", UUID.randomUUID().toString()));
        res.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void givenServiceThrows_whenGetCommentsForThread_thenStatusBadRequest() throws Exception {
        given(commentService.getCommentsForThread(any())).willThrow(new ForumException(ForumExceptions.INVALID_ID));
        var res = mockMvc.perform(MockMvcRequestBuilders.get("/api/comments")
                .param("id", UUID.randomUUID().toString()));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenServiceNotThrow_whenUpdateContent_thenStatusNoContent() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.put("/api/comments/content")
                .param("id", UUID.randomUUID().toString())
                .param("content", ""));
        res.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void givenServiceThrows_whenUpdateContent_thenStatusBadRequest() throws Exception {
        willThrow(new ForumException(ForumExceptions.INVALID_ID)).given(commentService).updateContent(any(), any());
        var res = mockMvc.perform(MockMvcRequestBuilders.put("/api/comments/content")
                .param("id", UUID.randomUUID().toString())
                .param("content", ""));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void givenServiceNotThrow_whenDeleteComment_thenStatusNoContent() throws Exception {
        var res = mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/delete")
                .param("id", UUID.randomUUID().toString()));
        res.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void givenServiceThrows_whenDeleteComment_thenStatusBadRequest() throws Exception {
        willThrow(new ForumException(ForumExceptions.INVALID_ID)).given(commentService).deleteComment(any());
        var res = mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/delete")
                .param("id", UUID.randomUUID().toString()));
        res.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }}
