package ft.projects.forum;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;

public abstract class AbstractIntegrationTest {

    @ServiceConnection
    public static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    static {
        mySQLContainer.start();
    }
}
