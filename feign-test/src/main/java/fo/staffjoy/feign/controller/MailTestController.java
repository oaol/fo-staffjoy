package fo.staffjoy.feign.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fo.staffjoy.mail.client.MailClient;
import fo.staffjoy.mail.dto.EmailRequest;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("mail")
public class MailTestController {

    private MailClient mailClient;

    @GetMapping
    public ResponseEntity<String> sendMail() {
        return mailClient.send(EmailRequest.builder().name("1").htmlBody("哈哈").subject("哈哈").to("哈哈").build());
    }
}
