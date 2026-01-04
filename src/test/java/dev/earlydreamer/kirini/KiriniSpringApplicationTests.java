package dev.earlydreamer.kirini;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = KiriniSpringApplication.class)
@ActiveProfiles("test")
class KiriniSpringApplicationTests {

    @Test
    void contextLoads() {
    }

}
