package tech.staffjoy.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.structlog4j.ILogger;
import com.github.structlog4j.SLoggerFactory;

import tech.staffjoy.account.client.AccountClient;
import tech.staffjoy.account.dto.AccountDto;
import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.auth.AuthContext;
import tech.staffjoy.common.env.EnvConfig;
import tech.staffjoy.common.env.EnvConstant;
import tech.staffjoy.common.exception.ServiceException;
import tech.staffjoy.company.client.CompanyClient;
import tech.staffjoy.company.dto.CompanyDto;
import tech.staffjoy.company.dto.CreateTeamRequest;
import tech.staffjoy.company.dto.DirectoryEntryDto;
import tech.staffjoy.company.dto.DirectoryEntryRequest;
import tech.staffjoy.company.dto.NewDirectoryEntry;
import tech.staffjoy.company.dto.TeamDto;
import tech.staffjoy.company.dto.WorkerDto;
import tech.staffjoy.web.service.HelperService;
import tech.staffjoy.web.view.Constant;
import tech.staffjoy.web.view.PageFactory;

@Controller
public class NewCompanyController {

    static final ILogger logger = SLoggerFactory.getLogger(LoginController.class);

    static final String DEFAULT_TIMEZONE = "UTC";
    static final String DEFAULT_DAYWEEK_STARTS = "Monday";
    static final String DEFAULT_TEAM_NAME = "Team";
    static final String DEFAULT_TEAM_COLOR = "#744fc6";

    @Autowired
    private PageFactory pageFactory;

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private HelperService helperService;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private CompanyClient companyClient;

    @RequestMapping(value = "/new-company")
    public String newCompany(@RequestParam(value="name", required = false) String name,
                             @RequestParam(value="timezone", required = false) String timezone,
                             @RequestParam(value="team", required = false) String teamName,
                             Model model) {
        if (StringUtils.isEmpty(AuthContext.getAuthz()) || AuthConstant.AUTHORIZATION_ANONYMOUS_WEB.equals(AuthContext.getAuthz())) {
            return "redirect:/login";
        }

        if(StringUtils.hasText(name)) {
            if (!StringUtils.hasText(timezone)) {
                timezone = DEFAULT_TIMEZONE;
            }
            if (!StringUtils.hasText(teamName)) {
                teamName = DEFAULT_TEAM_NAME;
            }

            // fetch current userId
            String currentUserId = AuthContext.getUserId();
            if (currentUserId == null) {
                throw new ServiceException("current userId not found in auth context");
            }

            AccountDto currentUser = null;
            ResponseEntity<AccountDto> accountResponse = null;
            try {
                accountResponse = accountClient.getAccount(AuthConstant.AUTHORIZATION_WWW_SERVICE, currentUserId);
            } catch(Exception ex) {
                String errMsg = "fail to get user account";
                helperService.logException(logger, ex, errMsg);
                throw new ServiceException(errMsg, ex);
            }
            if (accountResponse.getStatusCode().isError()) {
//                helperService.logError(logger, genericAccountResponse.getMessage());
                throw new ServiceException(accountResponse.getStatusCode().getReasonPhrase());
            } else {
                currentUser = accountResponse.getBody();
            }

            // Make the company
            ResponseEntity<CompanyDto> createCompanyResponse = null;
            try {
                CompanyDto companyDtoToCreate = CompanyDto.builder()
                        .name(name)
                        .defaultTimezone(timezone)
                        .defaultDayWeekStarts(DEFAULT_DAYWEEK_STARTS)
                        .build();
                createCompanyResponse = companyClient.createCompany(AuthConstant.AUTHORIZATION_WWW_SERVICE, companyDtoToCreate);
            } catch(Exception ex) {
                String errMsg = "fail to create company";
//                helperService.logException(logger, ex, errMsg);
                throw new ServiceException(errMsg, ex);
            }
            if (createCompanyResponse.getStatusCode().isError()) {
//                helperService.logError(logger, genericCompanyResponse.getMessage());
                throw new ServiceException(createCompanyResponse.getStatusCode().getReasonPhrase());
            }

            CompanyDto companyDto = createCompanyResponse.getBody();

            // register current user in directory
            ResponseEntity<DirectoryEntryDto> createDirectoryResponse = null;
            try {
                NewDirectoryEntry newDirectoryEntry = NewDirectoryEntry.builder()
                        .companyId(companyDto.getId())
                        .email(currentUser.getEmail())
                        .build();
                createDirectoryResponse = companyClient.createDirectory(AuthConstant.AUTHORIZATION_WWW_SERVICE, newDirectoryEntry);
            } catch(Exception ex) {
                String errMsg = "fail to create directory";
//                helperService.logException(logger, ex, errMsg);
                throw new ServiceException(errMsg, ex);
            }
            if (createDirectoryResponse.getStatusCode().isError()) {
//                helperService.logError(logger, genericDirectoryResponse1.getMessage());
                throw new ServiceException(createDirectoryResponse.getStatusCode().getReasonPhrase());
            }

            // create admin
            ResponseEntity<DirectoryEntryDto> createAdminReponse = null;
            try {
                DirectoryEntryRequest directoryEntryRequest = DirectoryEntryRequest.builder()
                        .companyId(companyDto.getId())
                        .userId(currentUserId)
                        .build();
                createAdminReponse = companyClient.createAdmin(AuthConstant.AUTHORIZATION_WWW_SERVICE, directoryEntryRequest);
            } catch(Exception ex) {
                String errMsg = "fail to create admin";
//                helperService.logException(logger, ex, errMsg);
                throw new ServiceException(errMsg, ex);
            }
            if (createAdminReponse.getStatusCode().isError()) {
//                helperService.logError(logger, genericDirectoryResponse2.getMessage());
                throw new ServiceException(createAdminReponse.getStatusCode().getReasonPhrase());
            }

            // create team
            ResponseEntity<TeamDto> createTeamResponse = null;
            try {
                CreateTeamRequest createTeamRequest = CreateTeamRequest.builder()
                        .companyId(companyDto.getId())
                        .name(teamName)
                        .color(DEFAULT_TEAM_COLOR)
                        .build();
                createTeamResponse = companyClient.createTeam(AuthConstant.AUTHORIZATION_WWW_SERVICE, createTeamRequest);
            } catch(Exception ex) {
                String errMsg = "fail to create team";
//                helperService.logException(logger, ex, errMsg);
                throw new ServiceException(errMsg, ex);
            }
            if (createTeamResponse.getStatusCode().isError()) {
//                helperService.logError(logger, teamResponse.getMessage());
                throw new ServiceException(createTeamResponse.getStatusCode().getReasonPhrase());
            }
            TeamDto teamDto = createTeamResponse.getBody();

            // register as worker
            ResponseEntity<WorkerDto> createWorkerResponse = null;
            try {
                WorkerDto workerDto = WorkerDto.builder()
                        .companyId(companyDto.getId())
                        .teamId(teamDto.getId())
                        .userId(currentUserId)
                        .build();
                createWorkerResponse = companyClient.createWorker(AuthConstant.AUTHORIZATION_WWW_SERVICE, workerDto);
            } catch(Exception ex) {
                String errMsg = "fail to create worker";
//                helperService.logException(logger, ex, errMsg);
                throw new ServiceException(errMsg, ex);
            }

            if (createWorkerResponse.getStatusCode().isError()) {
//                helperService.logError(logger, directoryResponse.getMessage());
                throw new ServiceException(createWorkerResponse.getStatusCode().getReasonPhrase());
            }

            // redirect
//            logger.info(String.format("new company signup - %s", companyDto));
            String url = helperService.buildUrl("http", "app." + envConfig.getExternalApex());

//            helperService.syncUserAsync(currentUserId);
//            helperService.trackEventAsync(currentUserId, "freetrial_created");

            if (EnvConstant.ENV_PROD.equals(envConfig.getName()) && !currentUser.isSupport()) {
                // Alert sales of a new account signup
                helperService.sendEmailAsync(currentUser, companyDto);
            }

            return "redirect:" + url;
        }

        model.addAttribute(Constant.ATTRIBUTE_NAME_PAGE, pageFactory.buildNewCompanyPage());
        return Constant.VIEW_NEW_COMPANY;
    }

}
