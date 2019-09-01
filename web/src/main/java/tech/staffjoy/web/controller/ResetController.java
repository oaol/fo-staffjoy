package tech.staffjoy.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tech.staffjoy.account.client.AccountClient;
import tech.staffjoy.web.service.HelperService;
import tech.staffjoy.web.view.Constant;
import tech.staffjoy.web.view.PageFactory;

@Controller
public class ResetController {

    public static final String PASSWORD_RESET_PATH = "/password-reset";

//    static final ILogger logger = SLoggerFactory.getLogger(ResetController.class);

    @Autowired
    private PageFactory pageFactory;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private HelperService helperService;

    @RequestMapping(value = PASSWORD_RESET_PATH)
    public String passwordReset(@RequestParam(value="email", required = false) String email,
                                Model model,
                                HttpServletRequest request) {

        // TODO google recaptcha handling ignored for training/demo purpose
        // reference : https://www.google.com/recaptcha

//        if (HelperService.isPost(request)) {
//            PasswordResetRequest passwordResetRequest = PasswordResetRequest.builder()
//                    .email(email)
//                    .build();
//            BaseResponse baseResponse = null;
//            try {
//                baseResponse = accountClient.requestPasswordReset(AuthConstant.AUTHORIZATION_WWW_SERVICE, passwordResetRequest);
//            } catch (Exception ex) {
//                String errMsg = "Failed password reset";
//                helperService.logException(logger, ex, errMsg);
//                throw new ServiceException(errMsg, ex);
//            }
//            if (!baseResponse.isSuccess()) {
//                helperService.logError(logger, baseResponse.getMessage());
//                throw new ServiceException(baseResponse.getMessage());
//            }
//
//            logger.info("Initiating password reset");
//
//            model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, pageFactory.buildResetConfirmPage());
//            return Constant.VIEW_CONFIRM;
//        }

        model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, pageFactory.buildResetPage());
        return Constant.VIEW_RESET;
    }
}
