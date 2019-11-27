package tech.staffjoy.mail.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.staffjoy.mail.MailConstant;
import tech.staffjoy.mail.config.AppConfig;
import tech.staffjoy.mail.dto.EmailRequest;

/**
 * 
 * @author bryce
 * @Date Aug 22, 2019
 */
@Slf4j
@AllArgsConstructor
@Service
public class MailSendService {

    IAcsClient iacsClient;

    @Async(AppConfig.ASYNC_EXECUTOR_NAME)
    public void sendMailAsync(EmailRequest req) {
        SingleSendMailRequest mailRequest = new SingleSendMailRequest();
        mailRequest.setAccountName(MailConstant.ACCOUNT_NAME);
        mailRequest.setFromAlias(MailConstant.FROM_NAME);
        mailRequest.setAddressType(1);
        // for 新加坡
//        mailRequest.setVersion("2017-06-22");
        mailRequest.setToAddress(req.getTo());
        mailRequest.setReplyToAddress(false);
        mailRequest.setSubject(req.getSubject());
        mailRequest.setHtmlBody(req.getHtmlBody());

        try {
            SingleSendMailResponse mailResponse = iacsClient.getAcsResponse(mailRequest);
            log.info("Successfully sent email - request id : {}", mailResponse.getRequestId());
        } catch (ClientException ex) {
            log.error("Unable to send email", ex);
//            Context sentryContext = sentryClient.getContext();
//            sentryContext.addTag("subject", req.getSubject());
//            sentryContext.addTag("to", req.getTo());
//            sentryClient.sendException(ex);
//            logger.error("Unable to send email ", ex, logContext);
        }
    }

}
