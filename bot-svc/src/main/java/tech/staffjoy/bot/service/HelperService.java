package tech.staffjoy.bot.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.json.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import tech.staffjoy.account.client.AccountClient;
import tech.staffjoy.account.dto.AccountDto;
import tech.staffjoy.bot.BotConstant;
import tech.staffjoy.bot.config.AppConfig;
import tech.staffjoy.bot.props.AppProps;
import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.env.EnvConfig;
import tech.staffjoy.common.exception.ServiceException;
import tech.staffjoy.company.client.CompanyClient;
import tech.staffjoy.company.dto.CompanyDto;
import tech.staffjoy.mail.client.MailClient;
import tech.staffjoy.mail.dto.EmailRequest;
import tech.staffjoy.sms.client.SmsClient;
import tech.staffjoy.sms.dto.SmsRequest;


@SuppressWarnings("Duplicates")
@Component
public class HelperService {

    static final ILogger logger = SLoggerFactory.getLogger(HelperService.class);

    @Autowired
    EnvConfig envConfig;

    @Autowired
    SmsClient smsClient;

    @Autowired
    MailClient mailClient;

    @Autowired
    AccountClient accountClient;

    @Autowired
    CompanyClient companyClient;

    @Autowired
    AppProps appProps;
/*
    @Autowired
    private SentryClient sentryClient;
*/
    static final String[] standardGreetings = {
            "Hi %s!",
            "Hey %s -",
            "Hello %s.",
            "Hey, %s!"
    };

    static String getGreet(String firstName) {
        return String.format(standardGreetings[new Random().nextInt(standardGreetings.length)], firstName);
    }

    static String getFirstName(String name) {
        if (StringUtils.isEmpty(name)) return "there";
        String[] names = name.split(" ");
        return names[0];
    }

    DispatchPreference getPreferredDispatch(AccountDto account) {
        if (appProps.isForceEmailPreference()) {
            return DispatchPreference.DISPATCH_EMAIL;
        }
        // todo - check user notification preferences
        if (!StringUtils.isEmpty(account.getPhoneNumber())) {
            return DispatchPreference.DISPATCH_SMS;
        }
        if (!StringUtils.isEmpty(account.getEmail())) {
            return DispatchPreference.DISPATCH_EMAIL;
        }
        return DispatchPreference.DISPATCH_UNAVAILABLE;
    }

    AccountDto getAccountById(String userId) {
        ResponseEntity<AccountDto> resp = null;
        try {
            resp = accountClient.getAccount(AuthConstant.AUTHORIZATION_BOT_SERVICE, userId);
        } catch (Exception ex) {
            String errMsg = "fail to get user";
            logger.error(errMsg, ex);
            //sentryClient.sendException(ex);
            throw new ServiceException(errMsg, ex);
        }
        if (!resp.getStatusCode().isError()) {
            logger.error(resp.getStatusCode().getReasonPhrase());
            //sentryClient.sendMessage(resp.getMessage());
            throw new ServiceException(resp.getStatusCode().getReasonPhrase());
        }
        return resp.getBody();
    }

    CompanyDto getCompanyById(String companyId) {
        ResponseEntity<CompanyDto> response = null;
        try {
            response = companyClient.getCompany(AuthConstant.AUTHORIZATION_BOT_SERVICE, companyId);
        } catch (Exception ex) {
            String errMsg = "fail to get company";
            logger.error(errMsg, ex);
            //sentryClient.sendException(ex);
            throw new ServiceException(errMsg, ex);
        }
        if (response.getStatusCode().isError()) {
            logger.error(response.getStatusCode().getReasonPhrase());
           // sentryClient.sendMessage(response.getMessage());
            throw new ServiceException(response.getStatusCode().getReasonPhrase());
        }
        return response.getBody();
    }

    void sendMail(String email, String name, String subject, String htmlBody) {
        EmailRequest emailRequest = EmailRequest.builder()
                .to(email)
                .name(name)
                .subject(subject)
                .htmlBody(htmlBody)
                .build();

        ResponseEntity<String> baseResponse = null;
        try {
            baseResponse = mailClient.send(emailRequest);
        } catch (Exception ex) {
            String errMsg = "Unable to send email";
            logger.error(errMsg, ex);
            //sentryClient.sendException(ex);
            throw new ServiceException(errMsg, ex);
        }
        if (baseResponse.getStatusCode().isError()) {
            logger.error(baseResponse.getStatusCode().getReasonPhrase());
            //sentryClient.sendMessage(baseResponse.getMessage());
            throw new ServiceException(baseResponse.getStatusCode().getReasonPhrase());
        }

    }

    void sendSms(String phoneNumber, String templateCode, String templateParam) {
        SmsRequest smsRequest = SmsRequest.builder()
                .to(phoneNumber)
                .templateCode(templateCode)
                .templateParam(templateParam)
                .build();

        ResponseEntity<String> baseResponse = null;
        try {
            baseResponse = smsClient.send(AuthConstant.AUTHORIZATION_BOT_SERVICE, smsRequest);
        } catch (Exception ex) {
            String errMsg = "could not send sms";
            logger.error(errMsg, ex);
            //sentryClient.sendException(ex);
            throw new ServiceException(errMsg, ex);
        }
        if (baseResponse.getStatusCode().isError()) {
            logger.error(baseResponse.getStatusCode().getReasonPhrase());
            //sentryClient.sendMessage(baseResponse.getMessage());
            throw new ServiceException(baseResponse.getStatusCode().getReasonPhrase());
        }
    }

    @Async(AppConfig.ASYNC_EXECUTOR_NAME)
    void smsGreetingAsync(String phoneNumber) {
        String templateCode = BotConstant.GREETING_SMS_TEMPLATE_CODE;
        String templateParam = "";
        this.sendSms(phoneNumber, templateCode, templateParam);
    }

    @Async(AppConfig.ASYNC_EXECUTOR_NAME)
    void mailGreetingAsync(AccountDto accountDto) {
        String email = accountDto.getEmail();
        String name = accountDto.getName();
        String subject = "Staffjoy Greeting";
        String htmlBody = BotConstant.GREETING_EMAIL_TEMPLATE;
        this.sendMail(email, name, subject, htmlBody);
    }

    @Async(AppConfig.ASYNC_EXECUTOR_NAME)
    void mailOnBoardAsync(AccountDto account, CompanyDto companyDto) {
        URI icalURI = null;
        try {
            icalURI = new URI(envConfig.getScheme(), "ical." + envConfig.getExternalApex(), String.format("/%s.ics", account.getId()), null);
        } catch (URISyntaxException e) {
            throw new ServiceException("Fail to build URI", e);
        }

        String greet = HelperService.getGreet(HelperService.getFirstName(account.getName()));
        String companyName = companyDto.getName();
        String icalUrl = icalURI.toString();
        String email = account.getEmail();
        String name = account.getName();

        String htmlBody = String.format(BotConstant.ONBOARDING_EMAIL_TEMPLATE, greet, companyName, icalUrl);
        String subject = "Onboarding Message";

        this.sendMail(email, name, subject, htmlBody);

        // todo - check if upcoming shifts, and if there are - send them
        logger.info(String.format("onboarded worker %s (%s) for company %s (%s)", account.getId(), account.getName(), companyDto.getId(), companyDto.getName()));
    }


    @Async(AppConfig.ASYNC_EXECUTOR_NAME)
    void smsOnboardAsync(AccountDto account, CompanyDto companyDto) {
        URI icalURI = null;
        try {
            icalURI = new URI(envConfig.getScheme(), "ical." + envConfig.getExternalApex(), String.format("/%s.ics", account.getId()), null);
        } catch (URISyntaxException e) {
            throw new ServiceException("Fail to build URI", e);
        }

        String templateParam1 = Json.createObjectBuilder()
                .add("greet", HelperService.getGreet(HelperService.getFirstName(account.getName())))
                .add("company_name", companyDto.getName())
                .build()
                .toString();

        String templateParam3 = Json.createObjectBuilder()
                .add("ical_url", icalURI.toString())
                .build()
                .toString();

        // TODO crate sms template on aliyun then update code
//        String[] onboardingMessages = {
//                String.format("%s Your manager just added you to %s on Staffjoy to share your work schedule.", HelperService.getGreet(HelperService.getFirstName(account.getName())), companyDto.getName()),
//                "When your manager publishes your shifts, we'll send them to you here. (To disable Staffjoy messages, reply STOP at any time)",
//                String.format("Click this link to sync your shifts to your phone's calendar app: %s", icalURI.toString())
//        };
        Map<String, String> onboardingMessageMap = new HashMap<String, String>() {{
            put(BotConstant.ONBOARDING_SMS_TEMPLATE_CODE_1, templateParam1);
            put(BotConstant.ONBOARDING_SMS_TEMPLATE_CODE_2, "");
            put(BotConstant.ONBOARDING_SMS_TEMPLATE_CODE_3, templateParam3);
        }};


        for(Map.Entry<String, String> entry : onboardingMessageMap.entrySet()) {
            String templateCode = entry.getKey();
            String templateParam = entry.getValue();

            this.sendSms(account.getPhoneNumber(), templateCode, templateParam);

            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(4));
            } catch (InterruptedException e) {
                logger.warn("InterruptedException", e);
            }
        }
        // todo - check if upcoming shifts, and if there are - send them
        logger.info(String.format("onboarded worker %s (%s) for company %s (%s)", account.getId(), account.getName(), companyDto.getId(), companyDto.getName()));
    }
}
