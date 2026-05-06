package sk.mvp.user_service.integration;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import sk.mvp.user_service.TestContainerConfig;
import sk.mvp.user_service.async.outbox.job.OutboxRealyJob;
import sk.mvp.user_service.auth.service.impl.AuthServiceImpl;

@SpringBootTest
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {
    @MockBean
    OutboxRealyJob realyJob;
}
