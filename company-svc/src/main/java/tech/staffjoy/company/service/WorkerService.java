package tech.staffjoy.company.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.staffjoy.common.exception.ServiceException;
import tech.staffjoy.company.dto.DirectoryEntryDto;
import tech.staffjoy.company.dto.TeamDto;
import tech.staffjoy.company.dto.WorkerDto;
import tech.staffjoy.company.dto.WorkerEntries;
import tech.staffjoy.company.dto.WorkerOfList;
import tech.staffjoy.company.model.Worker;
import tech.staffjoy.company.repository.WorkerRepo;
import tech.staffjoy.company.service.helper.ServiceHelper;

@Service
public class WorkerService {

//    static final ILogger logger = SLoggerFactory.getLogger(WorkerService.class);

    @Autowired
    WorkerRepo workerRepo;

    @Autowired
    TeamService teamService;

    @Autowired
    DirectoryService directoryService;

    @Autowired
    ServiceHelper serviceHelper;

    public WorkerEntries listWorkers(String companyId, String teamId) {
        // validate and will throw exception if not exist
        teamService.getTeamWithCompanyIdValidation(companyId, teamId);

        List<Worker> workerList = workerRepo.findByTeamId(teamId);

        WorkerEntries workerEntries = WorkerEntries.builder().companyId(companyId).teamId(teamId).build();
        for(Worker worker : workerList) {
            DirectoryEntryDto directoryEntryDto = directoryService.getDirectoryEntry(companyId, worker.getUserId());
            workerEntries.getWorkers().add(directoryEntryDto);
        }

        return workerEntries;
    }

    public DirectoryEntryDto getWorker(String companyId, String teamId, String userId) {
        // validate and throw exception if not exist
        teamService.getTeamWithCompanyIdValidation(companyId, teamId);

        Worker worker = workerRepo.findByTeamIdAndUserId(teamId, userId);
        if (worker == null) {
            throw new ServiceException("worker relationship not found");
        }

        DirectoryEntryDto directoryEntryDto = directoryService.getDirectoryEntry(companyId, userId);

        return directoryEntryDto;
    }

    public void deleteWorker(String companyId, String teamId, String userId) {
        // validate and throw exception if not found
        this.getWorker(companyId, teamId, userId);

        try {
            workerRepo.deleteWorker(teamId, userId);
        } catch (Exception ex) {
            String errMsg = "failed to delete worker in database";
//            serviceHelper.handleErrorAndThrowException(logger, ex, errMsg);
        }

//        LogEntry auditLog = LogEntry.builder()
//                .currentUserId(AuthContext.getUserId())
//                .authorization(AuthContext.getAuthz())
//                .targetType("worker")
//                .targetId(userId)
//                .companyId(companyId)
//                .teamId(teamId)
//                .build();
//
//        logger.info("removed worker", auditLog);

        serviceHelper.trackEventAsync("worker_deleted");
    }

    public WorkerOfList getWorkerOf(String userId) {
        List<Worker> workerList = workerRepo.findByUserId(userId);

        WorkerOfList workerOfList = WorkerOfList.builder().userId(userId).build();
        for(Worker worker : workerList) {
            TeamDto teamDto = teamService.getTeam(worker.getTeamId());
            workerOfList.getTeams().add(teamDto);
        }

        return workerOfList;
    }

    public DirectoryEntryDto createWorker(WorkerDto workerDto) {
        // validate and will throw exception if not found
        teamService.getTeamWithCompanyIdValidation(workerDto.getCompanyId(), workerDto.getTeamId());

        DirectoryEntryDto directoryEntryDto = directoryService.getDirectoryEntry(workerDto.getCompanyId(), workerDto.getUserId());

        Worker worker = workerRepo.findByTeamIdAndUserId(workerDto.getTeamId(), workerDto.getUserId());
        if (worker != null) {
            throw new ServiceException("user is already a worker");
        }

        try {
            Worker workerToCreate = Worker.builder().teamId(workerDto.getTeamId()).userId(workerDto.getUserId()).build();
            workerRepo.save(workerToCreate);
        } catch (Exception ex) {
            String errMsg = "failed to create worker in database";
//            serviceHelper.handleErrorAndThrowException(logger, ex, errMsg);
        }

//        LogEntry auditLog = LogEntry.builder()
//                .currentUserId(AuthContext.getUserId())
//                .authorization(AuthContext.getAuthz())
//                .targetType("worker")
//                .targetId(workerDto.getUserId())
//                .companyId(workerDto.getCompanyId())
//                .teamId(workerDto.getTeamId())
//                .build();
//
//        logger.info("added worker", auditLog);

        serviceHelper.trackEventAsync("worker_created");

        return directoryEntryDto;
    }
}
