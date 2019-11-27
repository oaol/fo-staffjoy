package tech.staffjoy.account.service;

import static java.util.stream.Collectors.toList;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import lombok.RequiredArgsConstructor;
import tech.staffjoy.account.AccountConstant;
import tech.staffjoy.account.dto.AccountDto;
import tech.staffjoy.account.dto.AccountList;
import tech.staffjoy.account.model.Account;
import tech.staffjoy.account.model.AccountSecret;
import tech.staffjoy.account.repository.AccountRepository;
import tech.staffjoy.account.repository.AccountSecretRepository;
import tech.staffjoy.account.service.helper.HelpService;
import tech.staffjoy.common.auditlog.LogEntry;
import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.auth.AuthContext;
import tech.staffjoy.common.crypto.Sign;
import tech.staffjoy.common.env.EnvConfig;
import tech.staffjoy.common.exception.ServiceException;
import tech.staffjoy.common.util.Helper;
import tech.staffjoy.mail.client.MailClient;
import tech.staffjoy.mail.dto.EmailRequest;

@Service
@RequiredArgsConstructor
public class AccountService {

    static final ILogger logger = SLoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    private final AccountSecretRepository accountSecretRepository;

    private final tech.staffjoy.account.properties.AppProps appProps;

    private final EnvConfig envConfig;

    private final MailClient mailClient;

    private final HelpService helpService;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    @PersistenceContext
    private EntityManager entityManager;

    // GetOrCreate is for internal use by other APIs to match a user based on their phonenumber or email.
    public AccountDto getOrCreate(String name, String email, String phoneNumber) {
        // rely on downstream permissions

        // check for existing user
        Account existingAccount = null;
        if (StringUtils.hasText(email)) {
            existingAccount = accountRepository.findAccountByEmail(email).get();
        }
        if (existingAccount == null && StringUtils.hasText(phoneNumber)) {
            existingAccount = accountRepository.findAccountByPhoneNumber(phoneNumber).get();
        }

        if (existingAccount != null) {
            return this.convertToDto(existingAccount);
        }
        return this.create(name, email, phoneNumber);
    }

    public AccountDto getAccountByPhoneNumber(String phoneNumber) {
        Account account = accountRepository.findAccountByPhoneNumber(phoneNumber).get();
        if (account == null) {
            // ResultCode.NOT_FOUND,
            throw new ServiceException("User with specified phonenumber not found");
        }
        return this.convertToDto(account);
    }

    public AccountDto create(String name, String email, String phoneNumber) {
        if (StringUtils.hasText(email)) {
            // Check to see if account exists
            Account foundAccount = accountRepository.findAccountByEmail(email).orElse(null);
            if (foundAccount != null) {
                throw new ServiceException("A user with that email already exists. Try a password reset");
            }
        }
        if (StringUtils.hasText(phoneNumber)) {
            Account foundAccount = accountRepository.findAccountByPhoneNumber(phoneNumber).get();
            if (foundAccount != null) {
                throw new ServiceException("A user with that phonenumber already exists. Try a password reset");
            }
        }

        // Column name/email/phone_number cannot be null
        if (name == null) {
            name = "";
        }
        if (email == null) {
            email = "";
        }
        if (phoneNumber == null) {
            phoneNumber = "";
        }

        Account account = Account.builder()
                .email(email).name(name).phoneNumber(phoneNumber)
                .build();
        account.setPhotoUrl(Helper.generateGravatarUrl(account.getEmail()));
        account.setMemberSince(Instant.now());

        try {
            accountRepository.save(account);
        } catch (Exception ex) {
            String errMsg = "Could not create user account";
            helpService.handleException(logger, ex, errMsg);
            throw new ServiceException(errMsg, ex);
        }

//        serviceHelper.syncUserAsync(account.getId());

        if (StringUtils.hasText(email)) {
            // Email confirmation

            String emailName = name;
            if (StringUtils.isEmpty(emailName)) {
                emailName = "there";
            }

            String subject = "Activate your Staffjoy account";
            this.sendEmail(account.getId(), email, emailName, subject, AccountConstant.ACTIVATE_ACCOUNT_TMPL, true);
        }

        // todo - sms onboarding (if worker??)

        LogEntry auditLog = LogEntry.builder()
                .authorization(AuthContext.getAuthz())
                .currentUserId(AuthContext.getUserId())
                .targetType("account")
                .targetId(account.getId())
                .updatedContents(account.toString())
                .build();

        logger.info("created account", auditLog);

        AccountDto accountDto = this.convertToDto(account);
        return accountDto;
    }

    public AccountList list(int offset, int limit) {
        if (limit <= 0) {
            limit = 10;
        }

        Pageable pageRequest = PageRequest.of(offset, limit);
        Page<Account> accountPage = accountRepository.findAll(pageRequest);
        List<AccountDto> accountDtoList = accountPage.getContent().stream().map(account -> convertToDto(account)).collect(toList());

        return AccountList.builder()
                .limit(limit)
                .offset(offset)
                .accounts(accountDtoList)
                .build();
    }

    public AccountDto get(String userId) {
        Account account = accountRepository.findAccountById(userId).get();
        if (account == null) {
            throw new ServiceException(String.format("User with id %s not found", userId));
        }
        return this.convertToDto(account);
    }

    public AccountDto update(AccountDto newAccountDto) {
        Account newAccount = this.convertToModel(newAccountDto);

        Account existingAccount = accountRepository.findAccountById(newAccount.getId()).get();
        if (existingAccount == null) {
            throw new ServiceException(String.format("User with id %s not found", newAccount.getId()));
        }
        entityManager.detach(existingAccount);

        if (!helpService.isAlmostSameInstant(newAccount.getMemberSince(), existingAccount.getMemberSince())) {
//            ResultCode.REQ_REJECT, 
            throw new ServiceException("You cannot modify the member_since date");
        }

        if (StringUtils.hasText(newAccount.getEmail()) && !newAccount.getEmail().equals(existingAccount.getEmail())) {
            Account foundAccount = accountRepository.findAccountByEmail(newAccount.getEmail()).get();
            if (foundAccount != null) {
                // ResultCode.REQ_REJECT
                throw new ServiceException("A user with that email already exists. Try a password reset");
            }
        }

        if (StringUtils.hasText(newAccount.getPhoneNumber()) && !newAccount.getPhoneNumber().equals(existingAccount.getPhoneNumber())) {
            Account foundAccount = accountRepository.findAccountByPhoneNumber(newAccount.getPhoneNumber()).get();
            if (foundAccount != null) {
                // ResultCode.REQ_REJECT
                throw new ServiceException("A user with that phonenumber already exists. Try a password reset");
            }
        }

        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            if (!existingAccount.isConfirmedAndActive() && newAccount.isConfirmedAndActive()) {
                // ResultCode.REQ_REJECT, 
                throw new ServiceException("You cannot activate this account");
            }
            if (existingAccount.isSupport() != newAccount.isSupport()) {
                throw new ServiceException( "You cannot change the support parameter");
            }
            if (!existingAccount.getPhotoUrl().equals(newAccount.getPhotoUrl())) {
                throw new ServiceException("You cannot change the photo through this endpoint (see docs)");
            }
            // User can request email change - not do it :-)
            if (!existingAccount.getEmail().equals(newAccount.getEmail())) {
                this.requestEmailChange(newAccount.getId(), newAccount.getEmail());
                // revert
                newAccount.setEmail(existingAccount.getEmail());
            }
        }

        newAccount.setPhotoUrl(Helper.generateGravatarUrl(newAccount.getEmail()));

        try {
            accountRepository.save(newAccount);
        } catch (Exception ex) {
            String errMsg = "Could not update the user account";
            helpService.handleException(logger, ex, errMsg);
            throw new ServiceException(errMsg, ex);
        }

        helpService.syncUserAsync(newAccount.getId());

        LogEntry auditLog = LogEntry.builder()
                .authorization(AuthContext.getAuthz())
                .currentUserId(AuthContext.getUserId())
                .targetType("account")
                .targetId(newAccount.getId())
                .originalContents(existingAccount.toString())
                .updatedContents(newAccount.toString())
                .build();

        logger.info("updated account", auditLog);

        // TODO
        // If account is being activated, or if phone number is changed by current user - send text
//        if (newAccount.isConfirmedAndActive() &&
//                StringUtils.hasText(newAccount.getPhoneNumber()) &&
//                !newAccount.getPhoneNumber().equals(existingAccount.getPhoneNumber())) {
////            serviceHelper.sendSmsGreeting(newAccount.getId());
//        }

        this.trackEventWithAuthCheck("account_updated");

        AccountDto accountDto = this.convertToDto(newAccount);
        return accountDto;
    }

    public void updatePassword(String userId, String password) {
        String pwHash = passwordEncoder.encode(password);

        int affected = accountSecretRepository.updatePasswordHashById(pwHash, userId);
        if (affected != 1) {
            // not found
            throw new ServiceException("user with specified id not found");
        }

        LogEntry auditLog = LogEntry.builder()
                .authorization(AuthContext.getAuthz())
                .currentUserId(AuthContext.getUserId())
                .targetType("account")
                .targetId(userId)
                .build();

        logger.info("updated password", auditLog);

        this.trackEventWithAuthCheck("password_updated");
    }

    public AccountDto verifyPassword(String email, String password) {
        AccountSecret accountSecret = accountSecretRepository.findAccountSecretByEmail(email).get();
        if (accountSecret == null) {
            // ResultCode.NOT_FOUND
            throw new ServiceException("account with specified email not found");
        }

        if (!accountSecret.isConfirmedAndActive()) {
            // ResultCode.REQ_REJECT
            throw new ServiceException("This user has not confirmed their account");
        }

        if (StringUtils.isEmpty(accountSecret.getPasswordHash())) {
//            ResultCode.REQ_REJECT
            throw new ServiceException("This user has not set up their password");
        }

        if (!passwordEncoder.matches(password, accountSecret.getPasswordHash())) {
            // ResultCode.UN_AUTHORIZED,
            throw new ServiceException("Incorrect password");
        }

        Account account = accountRepository.findAccountById(accountSecret.getId()).get();
        if (account == null) {
            throw new ServiceException(String.format("User with id %s not found", accountSecret.getId()));
        }

        // You shall pass
        AccountDto accountDto = this.convertToDto(account);
        return accountDto;
    }

    // RequestPasswordReset sends an email to a user with a password reset link
    public void requestPasswordReset(String email) {
        String newEmail = email.toLowerCase().trim();

        Account account = accountRepository.findAccountByEmail(email).get();
        if(account == null) {
            // ResultCode.NOT_FOUND,
            throw new ServiceException("No user with that email exists");
        }

        String subject = "Reset your Staffjoy password";
        boolean activate = false; // reset
        String tmpl = AccountConstant.RESET_PASSWORD_TMPL;
        if (!account.isConfirmedAndActive()) {
            // Not actually active - make some tweaks for activate instead of password reset
            activate = true; // activate
            subject = "Activate your Staffjoy account";
            tmpl = AccountConstant.ACTIVATE_ACCOUNT_TMPL;
        }

        // Send verification email
        this.sendEmail(account.getId(), email, account.getName(), subject, tmpl, activate);
    }

    // requestEmailChange sends an email to a user with a confirm email link
    public void requestEmailChange(String userId, String email) {
        Account account = accountRepository.findAccountById(userId).get();
        if (account == null) {
            // ResultCode.NOT_FOUND,
            throw new ServiceException(String.format("User with id %s not found", userId));
        }

        String subject = "Confirm Your New Email Address";
        this.sendEmail(account.getId(), email, account.getName(), subject, AccountConstant.CONFIRM_EMAIL_TMPL, true);
    }

    // ChangeEmail sets an account to active and updates its email. It is
    // used after a user clicks a confirmation link in their email.
    public void changeEmailAndActivateAccount(String userId, String email) {

        int affected = accountRepository.updateEmailAndActivateById(email, userId);
        if (affected != 1) {
            // ResultCode.NOT_FOUND,
            throw new ServiceException( "user with specified id not found");
        }

        helpService.syncUserAsync(userId);

//        LogEntry auditLog = LogEntry.builder()
//                .authorization(AuthContext.getAuthz())
//                .currentUserId(AuthContext.getUserId())
//                .targetType("account")
//                .targetId(userId)
//                .updatedContents(email)
//                .build();
//
//        logger.info("changed email", auditLog);

        this.trackEventWithAuthCheck("email_updated");
    }

    void sendEmail(String userId, String email, String name, String subject, String template, boolean activateOrConfirm) {
        String token = null;
        try {
            token = Sign.generateEmailConfirmationToken(userId, email, appProps.getSigningSecret());
        } catch(Exception ex) {
            String errMsg = "Could not create token";
//            serviceHelper.handleException(logger, ex, errMsg);
            throw new ServiceException(errMsg, ex);
        }

        String pathFormat = "/activate/%s";
        if (!activateOrConfirm) {
            pathFormat = "/reset/%s";
        }
        String path = String.format(pathFormat, token);
        URI link = null;
        try {
            link = new URI("http", "www." + envConfig.getExternalApex(),
                    path, null);
        } catch (URISyntaxException ex) {
            String errMsg = "Could not create activation url";
            if (!activateOrConfirm) {
                errMsg = "Could not create reset url";
            }
//            helpService.handleException(logger, ex, errMsg);
            throw new ServiceException(errMsg, ex);
        }

        String htmlBody = null;
        if (activateOrConfirm) { // active or confirm
            htmlBody = String.format(template, name, link.toString(), link.toString(), link.toString());
        } else { // reset
            htmlBody = String.format(template, link.toString(), link.toString());
        }

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
//            serviceHelper.handleException(logger, ex, errMsg);
            throw new ServiceException(errMsg, ex);
        }
        if (baseResponse.getStatusCode().isError()) {
//            serviceHelper.handleError(logger, baseResponse.getMessage());
            // TODO
            throw new ServiceException(baseResponse.getStatusCode().name());
        }
    }

    public void trackEvent(String userId, String eventName) {
//        serviceHelper.trackEventAsync(userId, eventName);
    }

    public void syncUser(String userId) {
//        serviceHelper.syncUserAsync(userId);
    }

    private AccountDto convertToDto(Account account) {
        return modelMapper.map(account, AccountDto.class);
    }

    private Account convertToModel(AccountDto accountDto) {
        return modelMapper.map(accountDto, Account.class);
    }

    private void trackEventWithAuthCheck(String eventName) {
        String userId = AuthContext.getUserId();
        if (StringUtils.isEmpty(userId)) {
            // Not an action performed by a normal user
            // (noop - not an view)
            return;
        }

        this.trackEvent(userId, eventName);
    }

}
