package ft.projects.forum;

import ft.projects.forum.model.ForumRole;
import ft.projects.forum.model.ForumUser;
import ft.projects.forum.repository.ForumUserRepository;
import ft.projects.forum.security.service.JwtService;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static ft.projects.forum.Constants.*;
import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ForumUserFlowTest extends AbstractIntegrationTest {

    private static String adminUserJwt;
    private static String testUserJwt;
    @LocalServerPort
    private int port;

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
    public static void clean(@Autowired ForumUserRepository userRepository) {
        userRepository.deleteAll();
    }

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
    }

    @Test
    public void givenValidAuth_whenLogin_thenStatusOk() {
        given()
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((TEST_USERNAME + ":" + TEST_PASSWORD).getBytes(StandardCharsets.UTF_8)))
                .log().all()
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(200);
    }

    @Test
    public void givenInvalidAuth_whenLogin_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("invalid".getBytes(StandardCharsets.UTF_8)))
                .log().all()
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenLogin_thenStatusUnauthorized() {
        given()
                .log().all()
                .when()
                .post("/api/users/login")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuthAndAdminRole_whenGetUsers_thenStatusOk() {
        given()
                .header("Authorization", "Bearer " + adminUserJwt)
                .log().all()
                .when()
                .get("/api/users")
                .then()
                .statusCode(200);
    }

    @Test
    public void givenValidAuthAndNoAdminRole_whenGetUsers_thenStatusForbidden() {
        given()
                .header("Authorization", "Bearer " + testUserJwt)
                .log().all()
                .when()
                .get("/api/users")
                .then()
                .statusCode(403);
    }

    @Test
    public void givenInvalidAuth_whenGetUsers_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .log().all()
                .when()
                .get("/api/users")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenGetUsers_thenStatusUnauthorized() {
        given()
                .log().all()
                .when()
                .get("/api/users")
                .then()
                .statusCode(401);
    }
}
