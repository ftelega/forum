package ft.projects.forum;

import ft.projects.forum.model.*;
import ft.projects.forum.repository.ForumCommentRepository;
import ft.projects.forum.repository.ForumThreadRepository;
import ft.projects.forum.repository.ForumUserRepository;
import ft.projects.forum.security.service.JwtService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.UUID;

import static ft.projects.forum.Constants.*;
import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ForumCommentFlowTest extends AbstractIntegrationTest {

    private static String testUserJwt;
    private static UUID testThreadId;
    private final ForumCommentRepository commentRepository;
    private final ForumUserRepository userRepository;
    @LocalServerPort
    private int port;

    @Autowired
    public ForumCommentFlowTest(ForumCommentRepository commentRepository, ForumUserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @BeforeAll
    public static void setup(
            @Autowired ForumUserRepository userRepository,
            @Autowired PasswordEncoder passwordEncoder,
            @Autowired JwtService jwtService,
            @Autowired ForumThreadRepository threadRepository) {
        userRepository.deleteAll();
        userRepository.save(ForumUser.builder()
                .username(TEST_USERNAME)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .timezone(TEST_TIMEZONE)
                .role(ForumRole.ROLE_USER)
                .build()
        );
        testThreadId = threadRepository.save(ForumThread.builder()
                .title(TEST_THREAD_TITLE)
                .content(TEST_THREAD_CONTENT)
                .publishedAt(Instant.now())
                .isClosed(false)
                .build()
        ).getUuid();
        testUserJwt = jwtService.getToken(TEST_USERNAME);
    }

    @AfterAll
    public static void clean(@Autowired ForumUserRepository userRepository) {
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        commentRepository.deleteAll();
    }

    private UUID createCommentAndSetOwnershipToTestUser() {
        var user = userRepository.findByUsername(TEST_USERNAME).get();
        return commentRepository.save(ForumComment.builder()
                .user(user)
                .build()
        ).getUuid();
    }

    @Test
    public void givenValidAuth_whenCreateComment_thenStatusNoContent() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .contentType(ContentType.JSON)
                .body(new ForumCommentRequest(testThreadId, TEST_COMMENT_CONTENT))
                .log().all()
                .when()
                .post("/api/comments/create")
                .then()
                .statusCode(204);
    }

    @Test
    public void givenInvalidAuth_whenCreateComment_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .log().all()
                .when()
                .post("/api/comments/create")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenCreateComment_thenStatusUnauthorized() {
        given()
                .log().all()
                .when()
                .post("/api/comments/create")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuth_whenGetCommentsForThread_thenStatusOk() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .param("id", testThreadId)
                .log().all()
                .when()
                .get("/api/comments")
                .then()
                .statusCode(200);
    }

    @Test
    public void givenInvalidAuth_whenGetCommentsForThread_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .param("id", testThreadId)
                .log().all()
                .when()
                .get("/api/comments")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenGetCommentsForThread_thenStatusUnauthorized() {
        given()
                .param("id", testThreadId)
                .log().all()
                .when()
                .get("/api/comments")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuth_whenUpdateContent_thenStatusNoContent() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .param("id", createCommentAndSetOwnershipToTestUser())
                .param("content", UUID.randomUUID().toString())
                .log().all()
                .when()
                .put("/api/comments/content")
                .then()
                .statusCode(204);
    }

    @Test
    public void givenInvalidAuth_whenUpdateContent_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .param("id", UUID.randomUUID().toString())
                .param("content", UUID.randomUUID().toString())
                .log().all()
                .when()
                .put("/api/comments/content")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenUpdateContent_thenStatusUnauthorized() {
        given()
                .param("id", UUID.randomUUID().toString())
                .param("content", UUID.randomUUID().toString())
                .log().all()
                .when()
                .put("/api/comments/content")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuth_whenDeleteComment_thenStatusNoContent() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .param("id", createCommentAndSetOwnershipToTestUser())
                .log().all()
                .when()
                .delete("/api/comments/delete")
                .then()
                .statusCode(204);
    }

    @Test
    public void givenInvalidAuth_whenDeleteComment_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .param("id", UUID.randomUUID().toString())
                .log().all()
                .when()
                .delete("/api/comments/delete")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenDeleteComment_thenStatusUnauthorized() {
        given()
                .param("id", UUID.randomUUID().toString())
                .log().all()
                .when()
                .delete("/api/comments/delete")
                .then()
                .statusCode(401);
    }
}
