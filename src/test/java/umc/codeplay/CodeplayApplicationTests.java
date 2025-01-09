package umc.codeplay;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@ComponentScan(basePackages = "umc.codeplay")
class CodeplayApplicationTests {

	@Test
	void contextLoads() {
	}

}
