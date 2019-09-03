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
import tech.staffjoy.common.validation.Group1;
import tech.staffjoy.common.validation.Group2;
import tech.staffjoy.company.dto.CompanyDto;
import tech.staffjoy.company.dto.CompanyList;
import tech.staffjoy.company.service.CompanyService;
import tech.staffjoy.company.service.PermissionService;

@RestController
@RequestMapping("/v1/company")
@Validated
public class CompanyController {

    @Autowired
    CompanyService companyService;

    @Autowired
    PermissionService permissionService;

    @PostMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE
    })
    public ResponseEntity<CompanyDto> createCompany(@RequestBody @Validated({Group2.class}) CompanyDto companyDto) {
        CompanyDto newCompanyDto = companyService.createCompany(companyDto);
        return ResponseEntity.ok(newCompanyDto);
    }

    @GetMapping(path = "/list")
    @Authorize(value = {AuthConstant.AUTHORIZATION_SUPPORT_USER})
    public ResponseEntity<CompanyList> listCompanies(@RequestParam int offset, @RequestParam int limit) {
        CompanyList companyList = companyService.listCompanies(offset, limit);
        return ResponseEntity.ok(companyList);
    }

    @GetMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_ACCOUNT_SERVICE,
            AuthConstant.AUTHORIZATION_BOT_SERVICE,
            AuthConstant.AUTHORIZATION_WHOAMI_SERVICE,
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER,
            AuthConstant.AUTHORIZATION_WWW_SERVICE,
            AuthConstant.AUTHORIZATION_ICAL_SERVICE
    })
    public ResponseEntity<CompanyDto> getCompany(@RequestParam("company_id") String companyId) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyDirectory(companyId);
        }
        CompanyDto companyDto = companyService.getCompany(companyId);
        return ResponseEntity.ok(companyDto);
    }

    @PutMapping
    @Authorize(value = {
            AuthConstant.AUTHORIZATION_AUTHENTICATED_USER,
            AuthConstant.AUTHORIZATION_SUPPORT_USER
    })
    public ResponseEntity<CompanyDto> updateCompany(@RequestBody @Validated({Group1.class}) CompanyDto companyDto) {
        if (AuthConstant.AUTHORIZATION_AUTHENTICATED_USER.equals(AuthContext.getAuthz())) {
            permissionService.checkPermissionCompanyAdmin(companyDto.getId());
        }
        CompanyDto updatedCompanyDto = companyService.updateCompany(companyDto);
        return ResponseEntity.ok(updatedCompanyDto);
    }
}
