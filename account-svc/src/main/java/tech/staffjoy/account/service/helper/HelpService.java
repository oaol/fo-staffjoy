package tech.staffjoy.account.service.helper;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.staffjoy.account.config.AppConfig;
import tech.staffjoy.account.model.Account;
import tech.staffjoy.account.repository.AccountRepository;
import tech.staffjoy.common.env.EnvConfig;
import tech.staffjoy.common.exception.ServiceException;

@RequiredArgsConstructor
@Component
@Slf4j
public class HelpService {

    static final ILogger logger = SLoggerFactory.getLogger(HelpService.class);

//    private final CompanyClient companyClient;

    private final AccountRepository accountRepository;

//    private final SentryClient sentryClient;

//    private final BotClient botClient;

    private final EnvConfig envConfig;

    /**
     * async user info to intercom
     * 
     * @param userId
     */
    @Async(AppConfig.ASYNC_EXECUTOR_NAME)
    public void syncUserAsync(String userId) {
        if (envConfig.isDebug()) {
            log.debug("intercom disabled in dev & test environment");
            return;
        }

        Account account = accountRepository.findAccountById(userId);
        if (account == null) {
            throw new ServiceException(String.format("User with id %s not found", userId));
        }
        if (StringUtils.isEmpty(account.getPhoneNumber()) && StringUtils.isEmpty(account.getEmail())) {
            log.info(String.format("skipping sync for user %s because no email or phonenumber", account.getId()));
            return;
        }

        // async to Intercom
//        // use a map to de-dupe
//        Map<String, CompanyDto> memberships = new HashMap<>();
//
//        ResponseEntity<WorkerOfList> workerOfResponse = null;
//        try {
//            workerOfResponse = companyClient.getWorkerOf(AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE, userId);
//        } catch(Exception ex) {
//            String errMsg = "could not fetch workOfList";
//            handleException(logger, ex, errMsg);
//            throw new ServiceException(errMsg, ex);
//        }
//        if (workerOfResponse.getStatusCode().isError()) {
//            handleError(logger, workerOfResponse.getStatusCode().getReasonPhrase());
//            throw new ServiceException(workerOfResponse.getStatusCode().getReasonPhrase());
//        }
//        WorkerOfList workerOfList = workerOfResponse.getBody();
//        boolean isWorker = workerOfList.getTeams().size() > 0;
//
//        for(TeamDto teamDto : workerOfList.getTeams()) {
//            ResponseEntity<CompanyDto> companyResponse = null;
//            try {
//                companyResponse = companyClient.getCompany(AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE, teamDto.getCompanyId());
//            } catch (Exception ex) {
//                String errMsg = "could not fetch companyDto from teamDto";
//                handleException(logger, ex, errMsg);
//                throw new ServiceException(errMsg, ex);
//            }
//
//            if (companyResponse.getStatusCode().isError()) {
//                handleError(logger, companyResponse.getStatusCode().getReasonPhrase());
//                throw new ServiceException(companyResponse.getStatusCode().getReasonPhrase());
//            }
//
//            CompanyDto companyDto = companyResponse.getBody();
//
//            memberships.put(companyDto.getId(), companyDto);
//        }
//
//        ResponseEntity<AdminOfList> adminOfResponse = null;
//        try {
//            adminOfResponse = companyClient.getAdminOf(AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE, userId);
//        } catch (Exception ex) {
//            String errMsg = "could not fetch adminOfList";
//            handleException(logger, ex, errMsg);
//            throw new ServiceException(errMsg, ex);
//        }
//        if (adminOfResponse.getStatusCode().isError()) {
//            handleError(logger, adminOfResponse.getStatusCode().getReasonPhrase());
//            throw new ServiceException(adminOfResponse.getStatusCode().getReasonPhrase());
//        }
//        AdminOfList adminOfList = adminOfResponse.getBody();
//
//        boolean isAdmin = adminOfList.getCompanies().size() > 0;
//        for(CompanyDto companyDto : adminOfList.getCompanies()) {
//            memberships.put(companyDto.getId(), companyDto);
//        }

//        User user = new User();
//        user.setUserId(account.getId());
//        user.setEmail(account.getEmail());
//        user.setName(account.getName());
//        user.setSignedUpAt(account.getMemberSince().toEpochMilli());
//        user.setAvatar(new Avatar().setImageURL(account.getPhotoUrl()));
//        user.setLastRequestAt(Instant.now().toEpochMilli());
//
//        user.addCustomAttribute(CustomAttribute.newBooleanAttribute("v2", true));
//        user.addCustomAttribute(CustomAttribute.newStringAttribute("phonenumber", account.getPhoneNumber()));
//        user.addCustomAttribute(CustomAttribute.newBooleanAttribute("confirmed_and_active", account.isConfirmedAndActive()));
//        user.addCustomAttribute(CustomAttribute.newBooleanAttribute("is_worker", isWorker));
//        user.addCustomAttribute(CustomAttribute.newBooleanAttribute("is_admin", isAdmin));
//        user.addCustomAttribute(CustomAttribute.newBooleanAttribute("is_staffjoy_support", account.isSupport()));

//        for(CompanyDto companyDto : memberships.values()) {
//            user.addCompany(new io.intercom.api.Company().setCompanyID(companyDto.getId()).setName(companyDto.getName()));
//        }

//        this.syncUserWithIntercom(user, account.getId());
    }

//    void syncUserWithIntercom(User user, String userId) {
//        try {
//            Map<String, String> params = Maps.newHashMap();
//            params.put("user_id", userId);
//
//            User existing = User.find(params);
//
//            if (existing != null) {
//                User.update(user);
//            } else {
//                User.create(user);
//            }
//
//            logger.debug("updated intercom");
//        } catch (Exception ex) {
//            String errMsg = "fail to create/update user on Intercom";
//            handleException(logger, ex, errMsg);
//            throw new ServiceException(errMsg, ex);
//        }
//    }

//    @Async(AppConfig.ASYNC_EXECUTOR_NAME)
//    public void trackEventAsync(String userId, String eventName) {
//        if (envConfig.isDebug()) {
//            logger.debug("intercom disabled in dev & test environment");
//            return;
//        }
//
//        Event event = new Event()
//                .setUserID(userId)
//                .setEventName("v2_" + eventName)
//                .setCreatedAt(Instant.now().toEpochMilli());
//
//        try {
//            Event.create(event);
//        } catch (Exception ex) {
//            String errMsg = "fail to create event on Intercom";
//            handleException(logger, ex, errMsg);
//            throw new ServiceException(errMsg, ex);
//        }
//
//        logger.debug("updated intercom");
//    }

//    public void sendSmsGreeting(String userId) {
//        BaseResponse baseResponse = null;
//        try {
//            GreetingRequest greetingRequest = GreetingRequest.builder().userId(userId).build();
//            baseResponse = botClient.sendSmsGreeting(greetingRequest);
//        } catch (Exception ex) {
//            String errMsg = "could not send welcome sms";
//            handleException(logger, ex, errMsg);
//            throw new ServiceException(errMsg, ex);
//        }
//        if (!baseResponse.isSuccess()) {
//            handleError(logger, baseResponse.getMessage());
//            throw new ServiceException(baseResponse.getMessage());
//        }
//    }

    // for time diff < 2s, treat them as almost same
    public boolean isAlmostSameInstant(Instant dt1, Instant dt2) {
        long diff = dt1.toEpochMilli() - dt2.toEpochMilli();
        diff = Math.abs(diff);
        if (diff < TimeUnit.SECONDS.toMillis(1)) {
            return true;
        }
        return false;
    }

    public void handleError(ILogger log, String errMsg) {
        log.error(errMsg);
//        if (!envConfig.isDebug()) {
//            sentryClient.sendMessage(errMsg);
//        }
    }

    public void handleException(ILogger log, Exception ex, String errMsg) {
        log.error(errMsg, ex);
//        if (!envConfig.isDebug()) {
//            sentryClient.sendException(ex);
//        }
    }
}
