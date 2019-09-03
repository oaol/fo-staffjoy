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
import tech.staffjoy.common.auth.PermissionDeniedException;
import tech.staffjoy.common.exception.ServiceException;
import tech.staffjoy.company.dto.AdminEntries;
import tech.staffjoy.company.dto.AdminOfList;
import tech.staffjoy.company.dto.DirectoryEntryDto;
import tech.staffjoy.company.dto.DirectoryEntryRequest;
import tech.staffjoy.company.service.AdminService;
import tech.staffjoy.company.service.PermissionService;

@RestController
@RequestMapping("/v1/company/admin")
@Validated
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    PermissionService permissionService;

    @GetMapping(path = "/list")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<AdminEntries> listAdmins(@RequestParam String companyId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(companyId);
        }
        AdminEntries adminEntries = adminService.listAdmins(companyId);
        return ResponseEntity.ok(adminEntries);
    }

    @GetMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public ResponseEntity<DirectoryEntryDto> getAdmin(@RequestParam String companyId, @RequestParam String userId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(companyId);
        }
        DirectoryEntryDto directoryEntryDto = adminService.getAdmin(companyId, userId);
        if (directoryEntryDto == null) {
            throw new ServiceException("admin relationship not found");
        }
        return ResponseEntity.ok(directoryEntryDto);
    }

    @PostMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public ResponseEntity<DirectoryEntryDto> createAdmin(@RequestBody @Validated DirectoryEntryRequest request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(request.getCompanyId());
        }
        DirectoryEntryDto directoryEntryDto = adminService.createAdmin(request.getCompanyId(), request.getUserId());
        return ResponseEntity.ok(directoryEntryDto);
    }

    @DeleteMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<String> deleteAdmin(@RequestBody @Validated DirectoryEntryRequest request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(request.getCompanyId());
        }
        adminService.deleteAdmin(request.getCompanyId(), request.getUserId());
        return ResponseEntity.ok("");
    }

    @GetMapping(path = "/admin-of")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE,
            AuthConstant.AUTHORIZATION_WHOAMI_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public ResponseEntity<AdminOfList> getAdminOf(@RequestParam String userId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            if (!userId.equals(AuthContext.getUserId())) {
                throw new PermissionDeniedException("You do not have access to this service");
            }
        }
        AdminOfList adminOfList = adminService.getAdminOf(userId);
        return ResponseEntity.ok(adminOfList);
    }
}
