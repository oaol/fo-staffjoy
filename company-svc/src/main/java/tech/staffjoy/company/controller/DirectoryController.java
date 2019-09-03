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
import tech.staffjoy.company.dto.AssociationList;
import tech.staffjoy.company.dto.DirectoryEntryDto;
import tech.staffjoy.company.dto.DirectoryList;
import tech.staffjoy.company.dto.NewDirectoryEntry;
import tech.staffjoy.company.service.DirectoryService;
import tech.staffjoy.company.service.PermissionService;

@RestController
@RequestMapping("/v1/company/directory")
@Validated
public class DirectoryController {
    @Autowired
    DirectoryService directoryService;

    @Autowired
    PermissionService permissionService;

    @PostMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public ResponseEntity<DirectoryEntryDto> createDirectory(@RequestBody @Validated NewDirectoryEntry request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(request.getCompanyId());
        }
        DirectoryEntryDto directoryEntryDto = directoryService.createDirectory(request);
        return ResponseEntity.ok(directoryEntryDto);
    }

    @GetMapping(path = "/list")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<DirectoryList> listDirectories(@RequestParam String companyId,
                                                 @RequestParam(defaultValue = "0") int offset,
                                                 @RequestParam(defaultValue = "0") int limit) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz()))  {
            permissionService.checkPermissionCompanyAdmin(companyId);
        }
        DirectoryList directoryList = directoryService.listDirectory(companyId, offset, limit);
        return ResponseEntity.ok(directoryList);
    }

    @GetMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WHOAMI_SERVICE,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public ResponseEntity<DirectoryEntryDto> getDirectoryEntry(@RequestParam String companyId, @RequestParam String userId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            // user can access their own entry
            if (!userId.equals(AuthContext.getUserId())) {
                permissionService.checkPermissionCompanyAdmin(companyId);
            }
        }
        DirectoryEntryDto directoryEntryDto = directoryService.getDirectoryEntry(companyId, userId);
        return ResponseEntity.ok(directoryEntryDto);
    }

    @PutMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<DirectoryEntryDto> updateDirectoryEntry(@RequestBody @Validated DirectoryEntryDto request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(request.getCompanyId());
        }
        DirectoryEntryDto directoryEntryDto = directoryService.updateDirectoryEntry(request);
        return ResponseEntity.ok(directoryEntryDto);
    }

    @GetMapping(path = "/associations")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<AssociationList> getAssociations(@RequestParam String companyId,
                                                  @RequestParam(defaultValue = "0") int offset,
                                                  @RequestParam(defaultValue = "0") int limit) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(companyId);
        }
        AssociationList associationList = directoryService.getAssociations(companyId, offset, limit);
        return ResponseEntity.ok(associationList);
    }
}
