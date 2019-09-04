package tech.staffjoy.company.controller;

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

import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.auth.AuthContext;
import tech.staffjoy.common.auth.Authorize;
import tech.staffjoy.company.dto.CreateTeamRequest;
import tech.staffjoy.company.dto.TeamDto;
import tech.staffjoy.company.dto.TeamList;
import tech.staffjoy.company.dto.WorkerDto;
import tech.staffjoy.company.service.PermissionService;
import tech.staffjoy.company.service.TeamService;

@RestController
@RequestMapping("/v1/company/team")
@Validated
public class TeamController {
    @Autowired
    TeamService teamService;

    @Autowired
    PermissionService permissionService;

    @PostMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public ResponseEntity<TeamDto> createTeam(@RequestBody @Validated CreateTeamRequest request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(request.getCompanyId());
        }

        TeamDto teamDto = this.teamService.createTeam(request);

        return ResponseEntity.ok(teamDto);
    }

    @GetMapping(path = "/list")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<TeamList> listTeams(@RequestParam String companyId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(companyId);
        }

        TeamList teamList = this.teamService.listTeams(companyId);

        return ResponseEntity.ok(teamList);
    }

    @GetMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE,
            AuthConstant.AUTHORIZATION_BOT_SERVICE,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_ICAL_SERVICE,
            AuthConstant.AUTHORIZATION_WHOAMI_SERVICE
    })
    public ResponseEntity<TeamDto> getTeam(@RequestParam String companyId, @RequestParam String teamId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionTeamWorker(companyId, teamId);
        }

        TeamDto teamDto = this.teamService.getTeamWithCompanyIdValidation(companyId, teamId);

        return ResponseEntity.ok(teamDto);
    }

    @PutMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<TeamDto> updateTeam(@RequestBody @Validated TeamDto teamDto) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(teamDto.getCompanyId());
        }

        TeamDto updatedTeamDto = this.teamService.updateTeam(teamDto);

        return ResponseEntity.ok(updatedTeamDto);
    }

    @GetMapping(path = "/worker-team-info")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_ICAL_SERVICE
    })
    public ResponseEntity<WorkerDto> getWorkerTeamInfo(@RequestParam(required = false) String companyId, @RequestParam String userId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            if (!userId.equals(AuthContext.getUserId())) { // user can access their own entry
                if (StringUtils.isEmpty(companyId)) {
                    // TODO
                    return ResponseEntity.status(500).body(null);
                }
                permissionService.checkPermissionCompanyAdmin(companyId);
            }
        }
        WorkerDto workerDto = this.teamService.getWorkerTeamInfo(userId);
        return ResponseEntity.ok(workerDto);
    }

}
