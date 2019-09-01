package tech.staffjoy.account.client;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import tech.staffjoy.account.AccountConstant;
import tech.staffjoy.account.dto.AccountDto;
import tech.staffjoy.account.dto.AccountList;
import tech.staffjoy.account.dto.CreateAccountRequest;
import tech.staffjoy.account.dto.EmailChangeRequest;
import tech.staffjoy.account.dto.EmailConfirmation;
import tech.staffjoy.account.dto.GetOrCreateRequest;
import tech.staffjoy.account.dto.PasswordResetRequest;
import tech.staffjoy.account.dto.SyncUserRequest;
import tech.staffjoy.account.dto.TestDto;
import tech.staffjoy.account.dto.TrackEventRequest;
import tech.staffjoy.account.dto.UpdatePasswordRequest;
import tech.staffjoy.account.dto.VerifyPasswordRequest;
import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.validation.PhoneNumber;

@FeignClient(name = AccountConstant.SERVICE_NAME, path = "/account/account", url = "${staffjoy.account-service-endpoint}")
// TODO Client side validation can be enabled as needed
// @Validated
public interface AccountClient {
    // @RequestParam 必须写，否则会被认定为 post 请求
    @GetMapping(path = "/test")
    ResponseEntity<TestDto> test(@RequestParam("name") String name);

    @PostMapping
    ResponseEntity<AccountDto> createAccount(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestBody @Valid CreateAccountRequest request);

    @PostMapping(path = "/track-event")
    ResponseEntity<String> trackEvent(@RequestBody @Valid TrackEventRequest request);

    @PostMapping(path = "/sync-user")
    ResponseEntity<String> syncUser(@RequestBody @Valid SyncUserRequest request);

    @GetMapping(path = "/list")
    ResponseEntity<AccountList> listAccounts(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestParam("offset") int offset, @RequestParam("limit") @Min(0) int limit);

    // GetOrCreate is for internal use by other APIs to match a user based on their
    // phonenumber or email.
    @PostMapping(path = "/selective")
    ResponseEntity<AccountDto> getOrCreateAccount(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestBody @Valid GetOrCreateRequest request);

    @GetMapping
    ResponseEntity<AccountDto> getAccount(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestParam("userId") @NotBlank String userId);

    @PutMapping
    ResponseEntity<AccountDto> updateAccount(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestBody @Valid AccountDto newAccount);

    @GetMapping(path = "/by-phonenumber")
    ResponseEntity<AccountDto> getAccountByPhonenumber(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestParam("phoneNumber") @PhoneNumber String phoneNumber);

    @PutMapping(path = "/password")
    ResponseEntity<String> updatePassword(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestBody @Valid UpdatePasswordRequest request);

    @PostMapping(path = "/verify-password")
    ResponseEntity<AccountDto> verifyPassword(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestBody @Valid VerifyPasswordRequest request);

    // RequestPasswordReset sends an email to a user with a password reset link
    @PostMapping(path = "/request-password-reset")
    ResponseEntity<String> requestPasswordReset(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestBody @Valid PasswordResetRequest request);

    @PostMapping(path = "/request-email-change")
    ResponseEntity<String> requetEmailChange(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestBody @Valid EmailChangeRequest request);

    // ChangeEmail sets an account to active and updates its email. It is
    // used after a user clicks a confirmation link in their email.
    @PostMapping(path = "/change-email")
    ResponseEntity<String> changeEmail(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz,
            @RequestBody @Valid EmailConfirmation request);
}
