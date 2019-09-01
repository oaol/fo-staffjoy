package fo.staffjoy.mail.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;

import lombok.extern.slf4j.Slf4j;
import tech.staffjoy.mail.MailConstant;
import tech.staffjoy.mail.client.MailClient;
import tech.staffjoy.mail.dto.EmailRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
//@DirtiesContext // avoid port conflict
@EnableFeignClients(basePackages = {"fo.staffjoy.mail.client"})
@Slf4j
public class MailControllerTest {
    @Autowired
    MailClient mailClient;

    @MockBean
    IAcsClient iAcsClient;

    @Test
    public void testSendMail() throws ClientException {

        SingleSendMailResponse singleSendMailResponse = new SingleSendMailResponse();
        singleSendMailResponse.setRequestId("mock_id");
        when(iAcsClient.getAcsResponse(any(SingleSendMailRequest.class))).thenReturn(singleSendMailResponse);

        String email = "black_star12@163.com";
        String name = "test_user";
        String subject = "test_subject";
        String htmlBody = "test html body...";
        EmailRequest emailRequest = EmailRequest.builder()
                .to(email)
                .name(name)
                .subject(subject)
                .htmlBody(htmlBody)
                .build();
        ResponseEntity<String> baseResponse = mailClient.send(emailRequest);
        
        log.info(baseResponse.toString());
        assertThat(baseResponse.getStatusCode().is2xxSuccessful()).isTrue();

        // verify email
        ArgumentCaptor<SingleSendMailRequest> argument = ArgumentCaptor.forClass(SingleSendMailRequest.class);
        verify(iAcsClient, times(1)).getAcsResponse(argument.capture());
        SingleSendMailRequest singleSendMailRequest = argument.getValue();
        assertThat(singleSendMailRequest.getAccountName()).isEqualTo(MailConstant.ACCOUNT_NAME);
        assertThat(singleSendMailRequest.getFromAlias()).isEqualTo(MailConstant.FROM_NAME);
        assertThat(singleSendMailRequest.getAddressType()).isEqualTo(1);
        assertThat(singleSendMailRequest.getToAddress()).isEqualTo(emailRequest.getTo());
        assertThat(singleSendMailRequest.getReplyToAddress()).isEqualTo(false);
        assertThat(singleSendMailRequest.getSubject()).endsWith(emailRequest.getSubject());
        assertThat(singleSendMailRequest.getHtmlBody()).isEqualTo(emailRequest.getHtmlBody());

        // aliyun fail
        when(iAcsClient.getAcsResponse(any(SingleSendMailRequest.class))).thenThrow(new ClientException("aliyun fail"));

        // even aliyun fail, send sms still succeed since async send
        baseResponse = mailClient.send(emailRequest);
        log.info(baseResponse.toString());
        assertThat(baseResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }
}
