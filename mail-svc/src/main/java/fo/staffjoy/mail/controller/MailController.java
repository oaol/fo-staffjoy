package fo.staffjoy.mail.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fo.staffjoy.mail.dto.EmailRequest;
import fo.staffjoy.mail.service.MailSendService;

/**
 * 
 * @author bryce
 * @Date Aug 13, 2019
 */

@RestController
@Validated
public class MailController {

    @Autowired
    private MailSendService mailSendService;

    @PostMapping(path = "/send")
    public ResponseEntity<String> send(@RequestBody @Valid EmailRequest request) {
        mailSendService.sendMailAsync(request);
        return ResponseEntity.ok("email has been sent async.");
    }

}
