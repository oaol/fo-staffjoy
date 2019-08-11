package fo.staffjoy.account.controller;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import fo.staffjoy.account.TestConfig;
import fo.staffjoy.account.client.AccountClient;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@EnableFeignClients(basePackages = {"fo.staffjoy.account.client"})
@Import(TestConfig.class)
public class AccountControllerTest {

    @Autowired
    AccountClient accountClient;
}
