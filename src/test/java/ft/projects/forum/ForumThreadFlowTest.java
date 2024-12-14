package ft.projects.forum;

import ft.projects.forum.model.ForumRole;
import ft.projects.forum.model.ForumThread;
import ft.projects.forum.model.ForumThreadRequest;
import ft.projects.forum.model.ForumUser;
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

import java.util.UUID;

import static ft.projects.forum.Constants.*;
import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ForumThreadFlowTest extends AbstractIntegrationTest {

    private static String adminUserJwt;
    private static String testUserJwt;
    private final ForumThreadRepository threadRepository;
    private final ForumUserRepository userRepository;
    @LocalServerPort
    private int port;

    @Autowired
    public ForumThreadFlowTest(ForumThreadRepository threadRepository, ForumUserRepository userRepository) {
        this.threadRepository = threadRepository;
        this.userRepository = userRepository;
    }

    @BeforeAll
    public static void setup(@Autowired ForumUserRepository userRepository, @Autowired PasswordEncoder passwordEncoder, @Autowired JwtService jwtService) {
        userRepository.deleteAll();
        userRepository.save(ForumUser.builder()
                .username(ADMIN_USERNAME)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .timezone(ADMIN_TIMEZONE)
                .role(ForumRole.ROLE_ADMIN)
                .build()
        );
        userRepository.save(ForumUser.builder()
                .username(TEST_USERNAME)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .timezone(TEST_TIMEZONE)
                .role(ForumRole.ROLE_USER)
                .build()
        );
        adminUserJwt = jwtService.getToken(ADMIN_USERNAME);
        testUserJwt = jwtService.getToken(TEST_USERNAME);
    }

    @AfterAll
    public static void clean(@Autowired ForumUserRepository userRepository, @Autowired ForumThreadRepository threadRepository) {
        threadRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        threadRepository.deleteAll();
    }

    private UUID createThreadAndSetOwnershipToTestUser() {
        var user = userRepository.findByUsername(TEST_USERNAME).get();
        return threadRepository.save(ForumThread.builder()
                .user(user)
                .build()
        ).getUuid();
    }

    @Test
    public void givenValidAuth_whenCreateThread_thenStatusNoContent() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .contentType(ContentType.JSON)
                .body(new ForumThreadRequest(TEST_THREAD_TITLE, TEST_THREAD_CONTENT))
                .log().all()
                .when()
                .post("/api/threads/create")
                .then()
                .statusCode(204);
    }

    @Test
    public void givenInvalidAuth_whenCreateThread_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .log().all()
                .when()
                .post("/api/threads/create")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenCreateThread_thenStatusUnauthorized() {
        given()
                .log().all()
                .when()
                .post("/api/threads/create")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuth_whenGetThreads_thenStatusOk() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .log().all()
                .when()
                .get("/api/threads")
                .then()
                .statusCode(200);
    }

    @Test
    public void givenInvalidAuth_whenGetThreads_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .log().all()
                .when()
                .get("/api/threads")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenGetThreads_thenStatusUnauthorized() {
        given()
                .log().all()
                .when()
                .get("/api/threads")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuth_whenUpdateContent_thenStatusNoContent() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .param("id", createThreadAndSetOwnershipToTestUser())
                .param("content", UUID.randomUUID().toString())
                .log().all()
                .when()
                .put("/api/threads/content")
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
                .put("/api/threads/content")
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
                .put("/api/threads/content")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuth_whenUpdateClosed_thenStatusNoContent() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .param("id", createThreadAndSetOwnershipToTestUser())
                .param("closed", "true")
                .log().all()
                .when()
                .put("/api/threads/closed")
                .then()
                .statusCode(204);
    }

    @Test
    public void givenInvalidAuth_whenUpdateClosed_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .param("id", UUID.randomUUID().toString())
                .param("closed", "true")
                .log().all()
                .when()
                .put("/api/threads/closed")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenUpdateClosed_thenStatusUnauthorized() {
        given()
                .param("id", UUID.randomUUID().toString())
                .param("closed", "true")
                .log().all()
                .when()
                .put("/api/threads/closed")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuth_whenDeleteThread_thenStatusNoContent() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .param("id", createThreadAndSetOwnershipToTestUser())
                .log().all()
                .when()
                .delete("/api/threads/delete")
                .then()
                .statusCode(204);
    }

    @Test
    public void givenInvalidAuth_whenDeleteThread_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .param("id", UUID.randomUUID().toString())
                .log().all()
                .when()
                .delete("/api/threads/delete")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenDeleteThread_thenStatusUnauthorized() {
        given()
                .param("id", UUID.randomUUID().toString())
                .log().all()
                .when()
                .delete("/api/threads/delete")
                .then()
                .statusCode(401);
    }
}
