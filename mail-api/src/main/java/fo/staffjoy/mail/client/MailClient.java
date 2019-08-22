package fo.staffjoy.mail.client;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import fo.staffjoy.mail.MailConstant;
import fo.staffjoy.mail.dto.EmailRequest;

@FeignClient(name = MailConstant.SERVICE_NAME, path = "/mail", url = "${staffjoy.email-service-endpoint}")
public interface MailClient {

    @PostMapping(path = "/send")
    ResponseEntity<String> send(@RequestBody @Valid EmailRequest request);

}
