package tech.staffjoy.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.auth.AuthContext;
import tech.staffjoy.common.auth.Authorize;
import tech.staffjoy.company.dto.DirectoryEntryDto;
import tech.staffjoy.company.dto.WorkerDto;
import tech.staffjoy.company.dto.WorkerEntries;
import tech.staffjoy.company.dto.WorkerOfList;
import tech.staffjoy.company.service.PermissionService;
import tech.staffjoy.company.service.WorkerService;

@RestController
@RequestMapping("/v1/company/worker")
@Validated
public class WorkerController {
    @Autowired
    WorkerService workerService;

    @Autowired
    PermissionService permissionService;

    @GetMapping(path = "/list")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<WorkerEntries> listWorkers(@RequestParam String companyId, @RequestParam String teamId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionTeamWorker(companyId, teamId);
        }
        WorkerEntries workerEntries = workerService.listWorkers(companyId, teamId);
        return ResponseEntity.ok(workerEntries);
    }

    @GetMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public ResponseEntity<DirectoryEntryDto> getWorker(@RequestParam  String companyId, @RequestParam String teamId, @RequestParam String userId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionTeamWorker(companyId, teamId);
        }
        DirectoryEntryDto directoryEntryDto = workerService.getWorker(companyId, teamId, userId);
        return ResponseEntity.ok(directoryEntryDto);
    }

    @DeleteMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<String> deleteWorker(@RequestBody @Validated WorkerDto workerDto) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(workerDto.getCompanyId());
        }
        workerService.deleteWorker(workerDto.getCompanyId(), workerDto.getTeamId(), workerDto.getUserId());
        return ResponseEntity.ok("worker has been deleted");
    }

    @GetMapping(path = "/worker-of")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            // This is an internal endpoint
            AuthConstant.AUTHORIZATION_WHOAMI_SERVICE
    })
    public ResponseEntity<WorkerOfList> getWorkerOf(@RequestParam String userId) {
        WorkerOfList workerOfList = workerService.getWorkerOf(userId);
        return ResponseEntity.ok(workerOfList);
    }

    @PostMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_WHOAMI_SERVICE
    })
    public ResponseEntity<DirectoryEntryDto> createWorker(@RequestBody @Validated WorkerDto workerDto) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(workerDto.getCompanyId());
        }
        DirectoryEntryDto directoryEntryDto = workerService.createWorker(workerDto);
        return ResponseEntity.ok(directoryEntryDto);
    }
}
