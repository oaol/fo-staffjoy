package fo.staffjoy.web.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fo.staffjoy.account.client.AccountClient;
import fo.staffjoy.account.dto.AccountDto;
import fo.staffjoy.account.dto.VerifyPasswordRequest;
import fo.staffjoy.common.auth.AuthConstant;
import fo.staffjoy.common.auth.AuthContext;
import fo.staffjoy.common.auth.Sessions;
import fo.staffjoy.common.env.EnvConfig;
import fo.staffjoy.common.services.Service;
import fo.staffjoy.common.services.ServiceDirectory;
import fo.staffjoy.web.properties.AppProps;
import fo.staffjoy.web.service.HelperService;
import fo.staffjoy.web.view.Constant;
import fo.staffjoy.web.view.LoginPage;
import fo.staffjoy.web.view.PageFactory;

@Controller
public class LoginController {

    @Autowired
    private PageFactory pageFactory;

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private AppProps appProps;

    @Autowired
    private HelperService helperService;

    @Autowired
    private AccountClient accountClient;

    @RequestMapping(value = "/login")
    public String login(@RequestParam(value="return_to", required = false) String returnTo, // POST and GET are in the same handler - reset
                        @RequestParam(value="email", required = false) String email,
                        @RequestParam(value="password", required = false) String password,
                        // rememberMe=True means that the session is set for a month instead of a day
                        @RequestParam(value="remember-me", required = false) String rememberMe,
                        Model model,
                        HttpServletRequest request,
                        HttpServletResponse response) {

        LoginPage loginPage = pageFactory.buildLoginPage();
        loginPage.setReturnTo(returnTo); // for GET

        // if logged in - go away
        if (!StringUtils.isEmpty(AuthContext.getAuthz()) && !AuthConstant.AUTHORIZATION_ANONYMOUS_WEB.equals(AuthContext.getAuthz())) {
            String url = HelperService.buildUrl("http", "myaccount." + envConfig.getExternalApex());
            return "redirect:" + url;
        }

        if (HelperService.isPost(request)) {

            AccountDto account = null;
            ResponseEntity<AccountDto> genericAccountResponse = null;
            try {
                VerifyPasswordRequest verifyPasswordRequest = VerifyPasswordRequest.builder()
                        .email(email)
                        .password(password)
                        .build();
                genericAccountResponse = accountClient.verifyPassword(AuthConstant.AUTHORIZATION_WWW_SERVICE, verifyPasswordRequest);
            } catch (Exception ex) {
                // TODO
//                helperService.logException(logger, ex, "fail to verify user password");
            }
            if (genericAccountResponse != null) {
                if (genericAccountResponse.getStatusCode().isError()) {
                    // TODO
//                    helperService.logError(logger, genericAccountResponse.getMessage());
                } else {
                    account = genericAccountResponse.getBody();
                }
            }

            if (account != null) { // login success
                // set cookie
                Sessions.loginUser(account.getId(),
                        account.isSupport(),
                        !StringUtils.isEmpty(rememberMe),
                        appProps.getSigningSecret(),
                        envConfig.getExternalApex(),
                        response);
//                helperService.trackEventAsync(account.getId(), "login");
//                helperService.syncUserAsync(account.getId());

                String scheme  = "https";
                if (envConfig.isDebug()) {
                    scheme = "http";
                }

                if (StringUtils.isEmpty(returnTo)) {
                    returnTo = HelperService.buildUrl(scheme, "app." + envConfig.getExternalApex());
                } else {
                    if (!returnTo.startsWith("http")) {
                        returnTo = "http://" + returnTo;
                    }
                    // sanitize
                    if (!isValidSub(returnTo)) {
                        returnTo = HelperService.buildUrl(scheme, "myaccount." + envConfig.getExternalApex());
                    }
                }

                return "redirect:" + returnTo;

            } else {
//                logger.info("Login attempt denied", "email", email);
                loginPage.setDenied(true);
                loginPage.setPreviousEmail(email);
            }

        }

        model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, loginPage);
        return Constant.VIEW_LOGIN;
    }

    // isValidSub returns true if url contains valid subdomain
    boolean isValidSub(String sub) {
        URL url = null;
        try {
            url = new URL(sub);
        } catch (MalformedURLException ex) {
//            logger.error("can't parse url", ex);
            return false;
        }

        String bare = url.getHost().replaceAll("." + envConfig.getExternalApex(), "");

        Map<String, Service> serviceMap = ServiceDirectory.getMapping();
        for(String key : serviceMap.keySet()) {
            if (key.equals(bare)) {
                return true;
            }
        }

        return false;
    }
}
