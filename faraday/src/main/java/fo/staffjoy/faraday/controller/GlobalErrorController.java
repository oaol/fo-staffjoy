package fo.staffjoy.faraday.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import fo.staffjoy.faraday.view.ErrorPage;
import fo.staffjoy.faraday.view.ErrorPageFactory;

@Controller
public class GlobalErrorController implements ErrorController{
    
    @Autowired
    ErrorPageFactory errorPageFactory;

    @RequestMapping("/error")
    public String handlerError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        ErrorPage errorPage = null;
        if (HttpStatus.FORBIDDEN.value() == (int)statusCode) {
            errorPage = errorPageFactory.buildForbiddenErrorPage();
        } else {
            errorPage = errorPageFactory.buildInternalServerErrorPage();
        }
        model.addAttribute("page", errorPage);
        return "error";
    }
    @Override
    public String getErrorPath() {
        return "/error";
    }

}
