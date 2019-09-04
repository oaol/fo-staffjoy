package tech.staffjoy.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import tech.staffjoy.company.dto.CreateJobRequest;
import tech.staffjoy.company.dto.JobDto;
import tech.staffjoy.company.dto.JobList;
import tech.staffjoy.company.service.JobService;
import tech.staffjoy.company.service.PermissionService;

@RestController
@RequestMapping("/v1/company/job")
@Validated
public class JobController {
    @Autowired
    JobService jobService;

    @Autowired
    PermissionService permissionService;

    @PostMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<JobDto> createJob(@RequestBody @Validated CreateJobRequest request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(request.getCompanyId());
        }

        JobDto jobDto = jobService.createJob(request);

        return ResponseEntity.ok(jobDto);
    }

    @GetMapping(path = "/list")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<JobList> listJobs(@RequestParam String companyId, @RequestParam String teamId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) { // TODO need confirm
            permissionService.checkPermissionTeamWorker(companyId, teamId);
        }

        JobList jobList = jobService.listJobs(companyId, teamId);

        return ResponseEntity.ok(jobList);
    }

    @GetMapping(path = "/get")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_BOT_SERVICE
    })
    public ResponseEntity<JobDto> getJob(String jobId, String companyId, String teamId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionTeamWorker(companyId, teamId);
        }

        JobDto jobDto = jobService.getJob(jobId, companyId, teamId);

        return ResponseEntity.ok(jobDto);
    }

    @PutMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<JobDto> updateJob(@RequestBody @Validated JobDto jobDto) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(jobDto.getCompanyId());
        }

        JobDto updatedJobDto = jobService.updateJob(jobDto);

        return ResponseEntity.ok(updatedJobDto);
    }
}
