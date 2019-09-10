package tech.staffjoy.sms.controller;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.auth.Authorize;
import tech.staffjoy.sms.dto.SmsRequest;
import tech.staffjoy.sms.props.AppProps;
import tech.staffjoy.sms.service.SmsSendService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@Validated
public class SmsController {

    static final ILogger logger = SLoggerFactory.getLogger(SmsController.class);

    @Autowired
    private AppProps appProps;

    @Autowired
    private SmsSendService smsSendService;

    @PostMapping(path = "/queue-send")
    @Authorize({
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE,
            AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE,
            AuthConstant.AUTHORIZATION_BOT_SERVICE
    })
    public ResponseEntity<String> send(@RequestBody @Valid SmsRequest smsRequest) {

        if (appProps.isWhiteListOnly()) {
            String whiteList = appProps.getWhiteListPhoneNumbers();
            boolean allowedToSend = !StringUtils.isEmpty(whiteList)
                    && whiteList.contains(smsRequest.getTo());
            if (!allowedToSend) {
                String msg = String.format("prevented sending to number %s due to whitelist", smsRequest.getTo());
                logger.warn(msg);
                 return ResponseEntity.ok(msg);
            }
        }

        smsSendService.sendSmsAsync(smsRequest);
        String msg = String.format("sent message to %s. async", smsRequest.getTo());
        logger.debug(msg);
        return ResponseEntity.ok("email has been sent async.");
    }

}
