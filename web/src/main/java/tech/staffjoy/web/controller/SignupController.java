package tech.staffjoy.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import tech.staffjoy.account.client.AccountClient;
import tech.staffjoy.account.dto.AccountDto;
import tech.staffjoy.account.dto.CreateAccountRequest;
import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.web.service.HelperService;
import tech.staffjoy.web.view.Constant;
import tech.staffjoy.web.view.PageFactory;

@Controller
public class SignupController {

    static final String SIGN_UP_REDIRECT_PATH = "redirect:/sign-up";

    private static final ILogger logger = SLoggerFactory.getLogger(SignupController.class);

    @Autowired
    private PageFactory pageFactory;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private HelperService helperService;

    @PostMapping(value="/confirm")
    public String signUp(@RequestParam(value = "name", required = false) String name, @RequestParam("email") String email, Model model) {
        if (!StringUtils.hasText(email)) {
            return SIGN_UP_REDIRECT_PATH;
        }

        CreateAccountRequest request = CreateAccountRequest.builder()
                .name(name)
                .email(email)
                .build();

        ResponseEntity<AccountDto> createAccountResponse = null;
        try {
            createAccountResponse = accountClient.createAccount(AuthConstant.AUTHORIZATION_WWW_SERVICE, request);
        } catch (Exception ex) {
            String errMsg = "Failed to create account";
            helperService.logException(logger, ex, errMsg);
            return SIGN_UP_REDIRECT_PATH;
        }

        if (createAccountResponse.getStatusCode().isError()) {
            helperService.logError(logger, createAccountResponse.getStatusCode().getReasonPhrase());
            return SIGN_UP_REDIRECT_PATH;
        }
        AccountDto account = createAccountResponse.getBody();
        logger.info(String.format("New Account signup - %s", account));

        model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, pageFactory.buildConfirmPage());
        return Constant.VIEW_CONFIRM;
    }
}
