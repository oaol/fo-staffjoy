package tech.staffjoy.company.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import tech.staffjoy.company.dto.BulkPublishShiftsRequest;
import tech.staffjoy.company.dto.CreateShiftRequest;
import tech.staffjoy.company.dto.ShiftDto;
import tech.staffjoy.company.dto.ShiftList;
import tech.staffjoy.company.dto.ShiftListRequest;
import tech.staffjoy.company.dto.WorkerShiftListRequest;
import tech.staffjoy.company.service.PermissionService;
import tech.staffjoy.company.service.ShiftService;

@RestController
@RequestMapping("/v1/company/shift")
@Validated
public class ShiftController {
    @Autowired
    ShiftService shiftService;

    @Autowired
    PermissionService permissionService;

    @PostMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<ShiftDto> createShift(@RequestBody @Validated CreateShiftRequest request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(request.getCompanyId());
        }

        ShiftDto shiftDto = this.shiftService.createShift(request);

        return ResponseEntity.ok(shiftDto);
    }

    @PostMapping(path = "/worker-shifts")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_BOT_SERVICE,
            AuthConstant.AUTHORIZATION_ICAL_SERVICE
    })
    public ResponseEntity<ShiftList> listWorkerShifts(@RequestBody @Validated WorkerShiftListRequest request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            // TODO need confirm
            permissionService.checkPermissionTeamWorker(request.getCompanyId(), request.getTeamId());
        }

        ShiftList shiftList = shiftService.listWorkerShifts(request);

        return ResponseEntity.ok(shiftList);
    }

    @PostMapping(path = "/shifts")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<ShiftList>  listShifts(@RequestBody @Validated ShiftListRequest request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionTeamWorker(request.getCompanyId(), request.getTeamId());
        }

        ShiftList shiftList = shiftService.listShifts(request);

        return ResponseEntity.ok(shiftList);
    }

    @PostMapping(path = "/bulk-publish")
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<ShiftList>  bulkPublishShifts(@RequestBody @Validated BulkPublishShiftsRequest request) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionTeamWorker(request.getCompanyId(), request.getTeamId());
        }

        ShiftList shiftList = shiftService.bulkPublishShifts(request);

        return ResponseEntity.ok(shiftList);
    }

    @GetMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<ShiftDto> getShift(@RequestParam String shiftId, @RequestParam String teamId, @RequestParam  String companyId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionTeamWorker(companyId, teamId);
        }

        ShiftDto shiftDto = shiftService.getShift(shiftId, teamId, companyId);

        return ResponseEntity.ok(shiftDto);
    }

    @PutMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<ShiftDto> updateShift(@RequestBody @Validated ShiftDto shiftDto) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(shiftDto.getCompanyId());
        }

        ShiftDto updatedShiftDto = shiftService.updateShift(shiftDto);

        return ResponseEntity.ok(updatedShiftDto);
    }

    @DeleteMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<String> deleteShift(@RequestParam String shiftId, @RequestParam String teamId, @RequestParam String companyId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionTeamWorker(companyId, teamId);
        }

        shiftService.deleteShift(shiftId, teamId, companyId);

        return ResponseEntity.ok("shift deleted");
    }
}
