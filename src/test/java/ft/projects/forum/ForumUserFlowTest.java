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

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static ft.projects.forum.Constants.*;
import static io.restassured.RestAssured.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ForumUserFlowTest extends AbstractIntegrationTest {

    private final ForumUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @LocalServerPort
    private int port;

    @Autowired
    public ForumUserFlowTest(ForumUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @AfterAll
    public static void clean(@Autowired ForumUserRepository userRepository, @Autowired JwtService jwtService) {
        userRepository.deleteAll();
        clearInvalidatedTokens(jwtService);
    }

    @BeforeEach
    public void setup() {
        RestAssured.port = port;
        userRepository.deleteAll();
        clearInvalidatedTokens(jwtService);
    }

    private String getAdminUserJwt() {
        userRepository.save(ForumUser.builder()
                .username(ADMIN_USERNAME)
                .password(passwordEncoder.encode(ADMIN_PASSWORD))
                .timezone(ADMIN_TIMEZONE)
                .role(ForumRole.ROLE_ADMIN)
                .build()
        );
        return jwtService.getToken(ADMIN_USERNAME);
    }

    private String getNormalUserJwt() {
        userRepository.save(ForumUser.builder()
                .username(TEST_USERNAME)
                .password(passwordEncoder.encode(TEST_PASSWORD))
                .timezone(TEST_TIMEZONE)
                .role(ForumRole.ROLE_USER)
                .build()
        );
        return jwtService.getToken(TEST_USERNAME);
    }

    private static void clearInvalidatedTokens(JwtService jwtService) {
        try {
            Field field = jwtService.getClass().getDeclaredField("INVALIDATED");
            field.setAccessible(true);
            var invalidatedTokens = (List<String>) field.get(null);
            invalidatedTokens.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenValidAuth_whenLogin_thenStatusOk() {
        getNormalUserJwt();
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
                .header("Authorization", "Bearer " + getAdminUserJwt())
                .log().all()
                .when()
                .get("/api/users")
                .then()
                .statusCode(200);
    }

    @Test
    public void givenValidAuthAndNoAdminRole_whenGetUsers_thenStatusForbidden() {
        given()
                .header("Authorization", "Bearer " + getNormalUserJwt())
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

    @Test
    public void givenValidAuth_whenUpdateUsername_thenStatusNoContentAndTokenInvalid() {
        var token = getNormalUserJwt();
        given()
                .header("Authorization", "Bearer " + token)
                .param("username", UUID.randomUUID())
                .log().all()
                .when()
                .put("/api/users/username")
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .get("/api/users")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenInvalidAuth_whenUpdateUsername_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .param("username", UUID.randomUUID())
                .log().all()
                .when()
                .put("/api/users/username")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenUpdateUsername_thenStatusUnauthorized() {
        given()
                .param("username", UUID.randomUUID())
                .log().all()
                .when()
                .put("/api/users/username")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuth_whenUpdatePassword_thenStatusNoContentAndTokenInvalid() {
        var token = getNormalUserJwt();
        given()
                .header("Authorization", "Bearer " + token)
                .param("password", UUID.randomUUID())
                .log().all()
                .when()
                .put("/api/users/password")
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .get("/api/users")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenInvalidAuth_whenUpdatePassword_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .param("password", UUID.randomUUID())
                .log().all()
                .when()
                .put("/api/users/password")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenUpdatePassword_thenStatusUnauthorized() {
        given()
                .param("password", UUID.randomUUID())
                .log().all()
                .when()
                .put("/api/users/password")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenValidAuth_whenDelete_thenStatusNoContentAndTokenInvalid() {
        var token = getNormalUserJwt();
        given()
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .delete("/api/users/delete")
                .then()
                .statusCode(204);

        given()
                .header("Authorization", "Bearer " + token)
                .log().all()
                .when()
                .get("/api/users")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenInvalidAuth_whenDelete_thenStatusUnauthorized() {
        given()
                .header("Authorization", "Bearer invalid")
                .log().all()
                .when()
                .delete("/api/users/delete")
                .then()
                .statusCode(401);
    }

    @Test
    public void givenNoAuth_whenDelete_thenStatusUnauthorized() {
         given()
                .log().all()
                .when()
                .delete("/api/users/delete")
                .then()
                .statusCode(401);
    }
}
