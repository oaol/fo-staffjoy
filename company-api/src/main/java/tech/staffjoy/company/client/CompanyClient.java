package tech.staffjoy.company.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import tech.staffjoy.common.auth.AuthConstant;
import tech.staffjoy.common.validation.Group1;
import tech.staffjoy.common.validation.Group2;
import tech.staffjoy.company.CompanyConstant;
import tech.staffjoy.company.dto.AdminEntries;
import tech.staffjoy.company.dto.AdminOfList;
import tech.staffjoy.company.dto.AssociationList;
import tech.staffjoy.company.dto.BulkPublishShiftsRequest;
import tech.staffjoy.company.dto.CompanyDto;
import tech.staffjoy.company.dto.CompanyList;
import tech.staffjoy.company.dto.CreateJobRequest;
import tech.staffjoy.company.dto.CreateShiftRequest;
import tech.staffjoy.company.dto.CreateTeamRequest;
import tech.staffjoy.company.dto.DirectoryEntryDto;
import tech.staffjoy.company.dto.DirectoryEntryRequest;
import tech.staffjoy.company.dto.DirectoryList;
import tech.staffjoy.company.dto.JobDto;
import tech.staffjoy.company.dto.JobList;
import tech.staffjoy.company.dto.NewDirectoryEntry;
import tech.staffjoy.company.dto.ShiftDto;
import tech.staffjoy.company.dto.ShiftList;
import tech.staffjoy.company.dto.ShiftListRequest;
import tech.staffjoy.company.dto.TeamDto;
import tech.staffjoy.company.dto.TeamList;
import tech.staffjoy.company.dto.WorkerDto;
import tech.staffjoy.company.dto.WorkerOfList;
import tech.staffjoy.company.dto.WorkerShiftListRequest;

@FeignClient(name = CompanyConstant.SERVICE_NAME, path = "/v1/company", url = "${staffjoy.company-service-endpoint}")
public interface CompanyClient {

    // Company Apis
    @PostMapping
    ResponseEntity<CompanyDto> createCompany(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated({Group2.class}) CompanyDto companyDto);

    @GetMapping(path = "/list")
    ResponseEntity<CompanyList> listCompanies(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("offset") int offset, @RequestParam("limit") int limit);

    @GetMapping
    ResponseEntity<CompanyDto> getCompany(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId);

    @PutMapping
    ResponseEntity<CompanyDto> updateCompany(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated({Group1.class}) CompanyDto companyDto);

    // Admin Apis
    @GetMapping(path = "/admin/list")
    ResponseEntity<AdminEntries> listAdmins(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId);

    @GetMapping(path = "/admin")
    ResponseEntity<DirectoryEntryDto> getAdmin(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId, @RequestParam("userId") String userId);

    // TODO
    @PostMapping(path = "/admin")
    ResponseEntity<DirectoryEntryDto> createAdmin(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated DirectoryEntryRequest request);

    @DeleteMapping(path = "/admin")
    ResponseEntity<String> deleteAdmin(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated DirectoryEntryRequest request);

    @GetMapping(path = "/admin/admin-of")
    ResponseEntity<AdminOfList> getAdminOf(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("userId") String userId);

    // Directory Apis
    @PostMapping(path = "/directory")
    ResponseEntity<DirectoryEntryDto> createDirectory(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated NewDirectoryEntry request);

    @GetMapping(path = "/directory/list")
    ResponseEntity<DirectoryList> listDirectories(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId, @RequestParam("offset") int offset, @RequestParam("limit") int limit);

    @GetMapping(path = "/directory")
    ResponseEntity<DirectoryEntryDto> getDirectoryEntry(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId, @RequestParam("userId") String userId);

    @PutMapping(path = "/directory")
    ResponseEntity<DirectoryEntryDto> updateDirectoryEntry(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated DirectoryEntryDto request);

    @GetMapping(path = "/directory/associations")
    ResponseEntity<AssociationList> getAssociations(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId, @RequestParam("offset") int offset, @RequestParam("limit") int limit);

    // WorkerDto Apis
    @GetMapping(path = "/worker/list")
    ResponseEntity<WorkerOfList> listWorkers(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId, @RequestParam("teamId") String teamId);

    @GetMapping(path = "/worker")
    ResponseEntity<WorkerDto> getWorker(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId")  String companyId, @RequestParam("teamId") String teamId, @RequestParam("userId") String userId);

    @DeleteMapping(path = "/worker")
    ResponseEntity<String> deleteWorker(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated WorkerDto workerDto);

    @GetMapping(path = "/worker/worker-of")
    ResponseEntity<WorkerOfList> getWorkerOf(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("userId") String userId);

    @PostMapping(path = "/worker")
    ResponseEntity<WorkerDto> createWorker(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated WorkerDto workerDto);

    // Team Apis
    @PostMapping(path = "/team")
    ResponseEntity<TeamDto> createTeam(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated CreateTeamRequest request);

    @GetMapping(path = "/team/list")
    ResponseEntity<TeamList> listTeams(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId);

    @GetMapping(path = "/team")
    ResponseEntity<TeamDto> getTeam(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId, @RequestParam("teamId") String teamId);

    @PutMapping(path = "/team")
    ResponseEntity<TeamDto> updateTeam(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated TeamDto teamDto);

    @GetMapping(path = "/team/worker-team-info")
    ResponseEntity<TeamDto> getWorkerTeamInfo(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam(required = false, value = "companyId") String companyId, @RequestParam("userId") String userId);

    // Job Apis
    @PostMapping(path = "/job")
    ResponseEntity<JobDto> createJob(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated CreateJobRequest request);

    @GetMapping(path = "/job/list")
    ResponseEntity<JobList> listJobs(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("companyId") String companyId, @RequestParam("teamId") String teamId);

    @GetMapping(path = "/job")
    ResponseEntity<JobDto> getJob(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("jobId") String jobId, @RequestParam("companyId") String companyId, @RequestParam("teamId") String teamId);

    @PutMapping(path = "/job")
    ResponseEntity<JobDto> updateJob(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated JobDto jobDto);

    // Shift Apis
    @PostMapping(path = "/shift")
    ResponseEntity<ShiftDto> createShift(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated CreateShiftRequest request);

    @PostMapping(path = "/shift/worker-shifts")
    ResponseEntity<ShiftList> listWorkerShifts(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated WorkerShiftListRequest request);

    @GetMapping(path = "/shift/shifts")
    ResponseEntity<ShiftList> listShifts(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated ShiftListRequest request);

    @PostMapping(path = "/shift/bulk-publish")
    ResponseEntity<ShiftList> bulkPublishShifts(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated BulkPublishShiftsRequest request);

    @GetMapping(path = "/shift")
    ResponseEntity<ShiftDto> getShift(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("shiftId") String shiftId, @RequestParam("teamId") String teamId, @RequestParam("companyId")  String companyId);

    @PutMapping(path = "/shift")
    ResponseEntity<ShiftDto> updateShift(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Validated ShiftDto shiftDto);

    @DeleteMapping(path = "/shift")
    ResponseEntity<String> deleteShift(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestParam("shiftId") String shiftId, @RequestParam("teamId") String teamId, @RequestParam("companyId") String companyId);
}
