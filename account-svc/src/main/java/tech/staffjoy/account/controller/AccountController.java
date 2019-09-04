package tech.staffjoy.account.controller;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import tech.staffjoy.account.dto.AccountDto;
import tech.staffjoy.account.dto.AccountList;
import tech.staffjoy.account.dto.CreateAccountRequest;
import tech.staffjoy.account.dto.EmailChangeRequest;
import tech.staffjoy.account.dto.EmailConfirmation;
import tech.staffjoy.account.dto.GetOrCreateRequest;
import tech.staffjoy.account.dto.PasswordResetRequest;
import tech.staffjoy.account.dto.SyncUserRequest;
import tech.staffjoy.account.dto.TrackEventRequest;
import tech.staffjoy.account.dto.UpdatePasswordRequest;
import tech.staffjoy.account.dto.VerifyPasswordRequest;
import tech.staffjoy.account.service.AccountService;
import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.auth.AuthContext;
import tech.staffjoy.common.auth.Authorize;
import tech.staffjoy.common.auth.PermissionDeniedException;
import tech.staffjoy.common.env.EnvConfig;
import tech.staffjoy.common.env.EnvConstant;
import tech.staffjoy.common.exception.ServiceException;
import tech.staffjoy.common.validation.PhoneNumber;

@RequestMapping("/account")
@RestController
@Validated
public class AccountController {
	
    private static final ILogger logger = SLoggerFactory.getLogger(AccountController.class);


    @Autowired
    private AccountService accountService;

    @Autowired
    private EnvConfig envConfig;

    // GetOrCreate is for internal use by other APIs to match a user based on their phonenumber or email.
    @PostMapping(path = "/selective")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE
    })
    public ResponseEntity<AccountDto> getOrCreate(@RequestBody @Valid GetOrCreateRequest request) {
        AccountDto accountDto = accountService.getOrCreate(request.getName(), request.getEmail(), request.getPhoneNumber());
        return ResponseEntity.ok(accountDto);
    }

    @PostMapping
    @Authorize(value = {
                    AuthConstant.AUTHORIZATION_SUPPORT_USER,
                    AuthConstant.AUTHORIZATION_WWW_SERVICE,
                    AuthConstant.AUTHORIZATION_COMPANY_SERVICE
    })
    public ResponseEntity<AccountDto> createAccount(@RequestBody @Valid CreateAccountRequest request) {
        AccountDto accountDto = accountService.create(request.getName(), request.getEmail(), request.getPhoneNumber());
        return ResponseEntity.ok(accountDto);
    }

    @GetMapping(path = "/by-phonenumber")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE
    })
    public ResponseEntity<AccountDto> getAccountByPhonenumber(@RequestParam @PhoneNumber String phoneNumber) {
        AccountDto accountDto = accountService.getAccountByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(accountDto);
    }

    @GetMapping(path = "/list")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<AccountList> listAccounts(@RequestParam int offset, @RequestParam @Min(0) int limit) {
        AccountList accountList = accountService.list(offset, limit);
        return ResponseEntity.ok(accountList);
    }

    @GetMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE,
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE,
            AuthConstant.AUTHORIZATION_WHOAMI_SERVICE,
            AuthConstant.AUTHORIZATION_BOT_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_SUPERPOWERS_SERVICE
    })
    public ResponseEntity<AccountDto> getAccount(@RequestParam @NotBlank String userId) {
        this.validateAuthenticatedUser(userId);
        this.validateEnv();

        AccountDto accountDto = accountService.get(userId);

        return ResponseEntity.ok(accountDto);
    }

    @PutMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_COMPANY_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_SUPERPOWERS_SERVICE
    })
    public ResponseEntity<AccountDto> updateAccount(@RequestBody @Valid AccountDto newAccountDto) {
        this.validateAuthenticatedUser(newAccountDto.getId());
        this.validateEnv();

        AccountDto accountDto =  accountService.update(newAccountDto);

        return ResponseEntity.ok(accountDto);
    }

    @PutMapping(path = "/password")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<String> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
        this.validateAuthenticatedUser(request.getUserId());

        accountService.updatePassword(request.getUserId(), request.getPassword());

        return ResponseEntity.ok("password updated");
    }

    @PostMapping(path = "/verify-password")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<AccountDto> verifyPassword(@RequestBody @Valid VerifyPasswordRequest request) {
        AccountDto accountDto = accountService.verifyPassword(request.getEmail(), request.getPassword());

        return ResponseEntity.ok(accountDto);
    }

    // RequestPasswordReset sends an email to a user with a password reset link
    @PostMapping(path = "/request-password-reset")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<String> requestPasswordReset(@RequestBody @Valid PasswordResetRequest request) {
        accountService.requestPasswordReset(request.getEmail());

        return ResponseEntity.ok("password reset requested");
    }

    // RequestPasswordReset sends an email to a user with a password reset link
    @PostMapping(path = "/request-email-change")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<String> requestEmailChange(@RequestBody @Valid EmailChangeRequest request) {
        this.validateAuthenticatedUser(request.getUserId());

        accountService.requestEmailChange(request.getUserId(), request.getEmail());

        return ResponseEntity.ok("email change requested");
    }

    // ChangeEmail sets an account to active and updates its email. It is
    // used after a user clicks a confirmation link in their email.
    @PostMapping(path = "/change-email")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<String> changeEmail(@RequestBody @Valid EmailConfirmation request) {
        accountService.changeEmailAndActivateAccount(request.getUserId(), request.getEmail());

        return ResponseEntity.ok("email change requested");
    }

    @PostMapping(path = "/track-event")
    public ResponseEntity<String> trackEvent(@RequestBody @Valid TrackEventRequest request) {
        accountService.trackEvent(request.getUserId(), request.getEvent());

        return ResponseEntity.ok("event tracked");
    }

    @PostMapping(path = "/sync-user")
    public ResponseEntity<String> syncUser(@RequestBody @Valid SyncUserRequest request) {
        accountService.syncUser(request.getUserId());

        return ResponseEntity.ok("user synced");
    }

    private void validateAuthenticatedUser(String userId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            String currentUserId = AuthContext.getUserId();
            if (StringUtils.isEmpty(currentUserId)) {
                throw new ServiceException("failed to find current user id");
            }
            if (!userId.equals(currentUserId)) {
                throw new PermissionDeniedException("You do not have access to this service");
            }
        }
    }

    private void validateEnv() {
        if (AuthConstant.AUTHORIZATION_SUPERPOWERS_SERVICE.equals(AuthContext.getAuthz())) {
            if (!EnvConstant.ENV_DEV.equals(this.envConfig.getName())) {
                logger.warn("Development service trying to connect outside development environment");
                throw new PermissionDeniedException("This service is not available outside development environments");
            }
        }
    }
}
