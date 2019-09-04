package tech.staffjoy.company.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import tech.staffjoy.common.exception.ServiceException;
import tech.staffjoy.company.dto.CreateTeamRequest;
import tech.staffjoy.company.dto.TeamDto;
import tech.staffjoy.company.dto.TeamList;
import tech.staffjoy.company.dto.WorkerDto;
import tech.staffjoy.company.model.Company;
import tech.staffjoy.company.model.Team;
import tech.staffjoy.company.model.Worker;
import tech.staffjoy.company.repository.CompanyRepo;
import tech.staffjoy.company.repository.TeamRepo;
import tech.staffjoy.company.repository.WorkerRepo;
import tech.staffjoy.company.service.helper.ServiceHelper;

@Service
public class TeamService {

//    static final ILogger logger = SLoggerFactory.getLogger(TeamService.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    TeamRepo teamRepo;

    @Autowired
    CompanyRepo companyRepo;

    @Autowired
    WorkerRepo workerRepo;

    @Autowired
    ServiceHelper serviceHelper;

    public TeamDto createTeam(CreateTeamRequest request) {
        Company company = companyRepo.findCompanyById(request.getCompanyId());
        if (company == null) {
            throw new ServiceException("Company with specified id not found");
        }

        // sanitize
        if (StringUtils.isEmpty(request.getDayWeekStarts())) {
            request.setDayWeekStarts(company.getDefaultDayWeekStarts());
        }
        if (StringUtils.isEmpty(request.getTimezone())) {
            request.setTimezone(company.getDefaultTimezone());
        }

        Team team = Team.builder()
                .companyId(request.getCompanyId())
                .name(request.getName())
                .dayWeekStarts(request.getDayWeekStarts())
                .timezone(request.getTimezone())
                .color(request.getColor())
                .build();

        try {
            teamRepo.save(team);
        } catch (Exception ex) {
            String errMsg = "could not create team";
//            serviceHelper.handleErrorAndThrowException(logger, ex, errMsg);
        }

//        LogEntry auditLog = LogEntry.builder()
//                .currentUserId(AuthContext.getUserId())
//                .authorization(AuthContext.getAuthz())
//                .targetType("team")
//                .targetId(team.getId())
//                .companyId(request.getCompanyId())
//                .teamId(team.getId())
//                .updatedContents(team.toString())
//                .build();
//
//        logger.info("created team", auditLog);

        serviceHelper.trackEventAsync("team_created");

        return convertToDto(team);
    }

    public TeamList listTeams(String companyId) {
        Company company = companyRepo.findCompanyById(companyId);
        if (company == null) {
            throw new ServiceException("Company with specified id not found");
        }

        List<Team> teams = teamRepo.findByCompanyId(companyId);

        TeamList teamList = TeamList.builder().build();
        for(Team team : teams) {
            TeamDto teamDto = this.getTeamWithCompanyIdValidation(team.getCompanyId(), team.getId());
            teamList.getTeams().add(teamDto);
        }

        return teamList;
    }

    public TeamDto getTeamWithCompanyIdValidation(String companyId, String teamId) {
        Company company = companyRepo.findCompanyById(companyId);
        if (company == null) {
            throw new ServiceException("Company with specified id not found");
        }

        return this.getTeam(teamId);
    }

    public TeamDto getTeam(String teamId) {
        Team team = teamRepo.findById(teamId).orElse(null);
        if (team == null) {
            throw new ServiceException("team with specified id not found");
        }
        return this.convertToDto(team);
    }

    public TeamDto updateTeam(TeamDto teamToUpdate) {
        TeamDto orig = this.getTeamWithCompanyIdValidation(teamToUpdate.getCompanyId(), teamToUpdate.getId());
        Team team = this.convertToModel(teamToUpdate);

        try {
            teamRepo.save(team);
        } catch (Exception ex) {
            String errMsg = "could not update the team";
//            serviceHelper.handleErrorAndThrowException(logger, ex, errMsg);
        }

//        LogEntry auditLog = LogEntry.builder()
//                .currentUserId(AuthContext.getUserId())
//                .authorization(AuthContext.getAuthz())
//                .targetType("team")
//                .targetId(orig.getId())
//                .companyId(teamToUpdate.getCompanyId())
//                .teamId(orig.getId())
//                .originalContents(orig.toString())
//                .updatedContents(teamToUpdate.toString())
//                .build();
//
//        logger.info("updated team", auditLog);

        serviceHelper.trackEventAsync("team_updated");

        return teamToUpdate;
    }

    // GetWorkerTeamInfo is an internal API method that given a worker UUID will
    // return team and company UUID - it's expected in the future that a
    // worker might belong to multiple teams/companies so this will prob.
    // need to be refactored at some point
    public WorkerDto getWorkerTeamInfo(String userId) {

        List<Worker> workers = workerRepo.findByUserId(userId);

        if (workers.size() == 0) {
            throw new ServiceException("worker with specified user id not found");
        }

        Worker worker = workers.get(0);

        TeamDto team = this.getTeam(worker.getTeamId());

        WorkerDto workerDto = WorkerDto.builder()
                .teamId(worker.getTeamId())
                .userId(worker.getUserId())
                .companyId(team.getCompanyId())
                .build();

        return workerDto;
    }

    private TeamDto convertToDto(Team team) {
        return modelMapper.map(team, TeamDto.class);
    }

    private Team convertToModel(TeamDto teamDto) {
        return modelMapper.map(teamDto, Team.class);
    }
}
