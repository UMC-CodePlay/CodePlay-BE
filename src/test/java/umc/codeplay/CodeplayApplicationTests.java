package umc.codeplay;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

import org.junit.jupiter.api.Test;

@ActiveProfiles("test")
@SpringBootTest
@ComponentScan(basePackages = "umc.codeplay")
class CodeplayApplicationTests {

    @Test
    void contextLoads() {}
}
