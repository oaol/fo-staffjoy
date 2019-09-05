package tech.staffjoy.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import tech.staffjoy.account.client.AccountClient;
import tech.staffjoy.account.dto.AccountDto;
import tech.staffjoy.account.dto.UpdatePasswordRequest;
import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.auth.Sessions;
import tech.staffjoy.common.crypto.Sign;
import tech.staffjoy.common.env.EnvConfig;
import tech.staffjoy.common.exception.ServiceException;
import tech.staffjoy.company.client.CompanyClient;
import tech.staffjoy.company.dto.AdminOfList;
import tech.staffjoy.company.dto.WorkerOfList;
import tech.staffjoy.web.properties.AppProps;
import tech.staffjoy.web.service.HelperService;
import tech.staffjoy.web.view.ActivatePage;
import tech.staffjoy.web.view.Constant;
import tech.staffjoy.web.view.PageFactory;

@Controller
public class ActivateController {
	
	private static final ILogger logger = SLoggerFactory.getLogger(ActivateController.class);
   
	@Autowired
    private PageFactory pageFactory;

    @Autowired
    private AppProps appProps;

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private HelperService helperService;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private CompanyClient companyClient;

    @RequestMapping(value = "/activate/{token}")
    public String activate(@PathVariable String token,
                           @RequestParam(value="password", required = false) String password,
                           @RequestParam(value="name", required = false) String name,
                           @RequestParam(value="tos", required = false) String tos,
                           @RequestParam(value="phonenumber", required = false) String phonenumber,
                           Model model,
                           HttpServletRequest request,
                           HttpServletResponse response) {

        ActivatePage page = pageFactory.buildActivatePage();
        page.setToken(token);

        String email = null;
        String userId = null;
        try {
            DecodedJWT jwt = Sign.verifyEmailConfirmationToken(token, appProps.getSigningSecret());
            email = jwt.getClaim(Sign.CLAIM_EMAIL).asString();
            userId = jwt.getClaim(Sign.CLAIM_USER_ID).asString();
        } catch (Exception ex) {
            String errMsg = "Failed to verify email confirmation token";
            helperService.logException(logger, ex, errMsg);
            return "redirect:" + ResetController.PASSWORD_RESET_PATH;
        }

        ResponseEntity<AccountDto> accountResponse = null;
        try {
            accountResponse = accountClient.getAccount(AuthConstant.AUTHORIZATION_WWW_SERVICE, userId);
        } catch (Exception ex) {
            String errMsg = "fail to get user account";
            helperService.logException(logger, ex, errMsg);
            throw new ServiceException(errMsg, ex);
        }
        if (accountResponse.getStatusCode().isError()) {
            helperService.logError(logger, accountResponse.getStatusCode().getReasonPhrase());
            throw new ServiceException(accountResponse.getStatusCode().getReasonPhrase());
        }
        AccountDto account = accountResponse.getBody();

        page.setEmail(email);
        page.setName(account.getName());
        page.setPhonenumber(account.getPhoneNumber());

        logger.info("pagemessage", "email",page.getEmail(),"name",page.getName(),"phoneNumber",page.getPhonenumber());
        if (!HelperService.isPost(request)) {
            model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, page);
            return Constant.VIEW_ACTIVATE;
        }

        // POST
        // update form in case we fail
        page.setName(name);
        page.setPhonenumber(phonenumber);

        if (password.length() < 6) {
            page.setErrorMessage("Your password must be at least 6 characters long");
        }

        if (StringUtils.isEmpty(tos)) {
            page.setErrorMessage("You must agree to the terms and conditions by selecting the checkbox.");
        }

        if (page.getErrorMessage() != null) {
            model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, page);
            return Constant.VIEW_ACTIVATE;
        }

        account.setEmail(email);
        account.setConfirmedAndActive(true);
        account.setName(name);
        account.setPhoneNumber(phonenumber);

        ResponseEntity<AccountDto> updateAccountResponse = null;
        try {
            updateAccountResponse = accountClient.updateAccount(AuthConstant.AUTHORIZATION_WWW_SERVICE, account);
        } catch (Exception ex) {
            String errMsg = "fail to update user account";
            helperService.logException(logger, ex, errMsg);
            page.setErrorMessage(errMsg);
            model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, page);
            return Constant.VIEW_ACTIVATE;
        }
        if (updateAccountResponse.getStatusCode().isError()) {
            helperService.logError(logger, updateAccountResponse.getStatusCode().getReasonPhrase());
            page.setErrorMessage(updateAccountResponse.getStatusCode().getReasonPhrase());
            model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, page);
            return Constant.VIEW_ACTIVATE;
        }

        // Update password
        ResponseEntity<String> updatePasswordResponse = null;
        try {
            UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
                    .userId(userId)
                    .password(password)
                    .build();
            updatePasswordResponse = accountClient.updatePassword(AuthConstant.AUTHORIZATION_WWW_SERVICE, updatePasswordRequest);
        } catch (Exception ex) {
            String errMsg = "fail to update password";
            helperService.logException(logger, ex, errMsg);
            page.setErrorMessage(errMsg);
            model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, page);
            return Constant.VIEW_ACTIVATE;
        }
        if (updatePasswordResponse.getStatusCode().isError()) {
            helperService.logError(logger, updatePasswordResponse.getStatusCode().getReasonPhrase());
            page.setErrorMessage(updatePasswordResponse.getStatusCode().getReasonPhrase());
            model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, page);
            return Constant.VIEW_ACTIVATE;
        }

        // login user
        Sessions.loginUser(account.getId(),
                account.isSupport(),
                false,
                appProps.getSigningSecret(),
                envConfig.getExternalApex(),
                response);
        logger.info("user activated account and logged in", "user_id", account.getId());


        // TODO
        // Smart redirection - for onboarding purposes
        ResponseEntity<WorkerOfList> workerOfResponse = null;
        try {
            workerOfResponse = companyClient.getWorkerOf(AuthConstant.AUTHORIZATION_WWW_SERVICE, account.getId());
        } catch (Exception ex) {
            String errMsg = "fail to get worker of list";
            helperService.logException(logger, ex, errMsg);
            throw new ServiceException(errMsg, ex);
        }
        if (workerOfResponse.getStatusCode().isError()) {
            helperService.logError(logger, workerOfResponse.getStatusCode().getReasonPhrase());
            throw new ServiceException(workerOfResponse.getStatusCode().getReasonPhrase());
        }
        WorkerOfList workerOfList = workerOfResponse.getBody();

        ResponseEntity<AdminOfList> adminOfReponse = null;
        try {
            adminOfReponse = companyClient.getAdminOf(AuthConstant.AUTHORIZATION_WWW_SERVICE, account.getId());
        } catch (Exception ex) {
            String errMsg = "fail to get admin of list";
            helperService.logException(logger, ex, errMsg);
            throw new ServiceException(errMsg, ex);
        }
        if (adminOfReponse.getStatusCode().isError()) {
            helperService.logError(logger, adminOfReponse.getStatusCode().getReasonPhrase());
            throw new ServiceException(adminOfReponse.getStatusCode().getReasonPhrase());
        }
        AdminOfList adminOfList = adminOfReponse.getBody();

        String destination = null;
        if (adminOfList.getCompanies().size() != 0 || account.isSupport()) {
            destination = helperService.buildUrl("http", "app." + envConfig.getExternalApex());
        } else if (workerOfList.getTeams().size() != 0) {
            destination = helperService.buildUrl("http", "myaccount." + envConfig.getExternalApex());
        } else {
            // onboard
            destination = helperService.buildUrl("http", "www." + envConfig.getExternalApex(), "/new-company/");
        }

        return "redirect:" + destination;
    }
}
