package tech.staffjoy.company.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tech.staffjoy.common.exception.ServiceException;
import tech.staffjoy.company.dto.CreateJobRequest;
import tech.staffjoy.company.dto.JobDto;
import tech.staffjoy.company.dto.JobList;
import tech.staffjoy.company.model.Job;
import tech.staffjoy.company.repository.JobRepo;
import tech.staffjoy.company.service.helper.ServiceHelper;

@Service
public class JobService {
//    static final ILogger logger = SLoggerFactory.getLogger(JobService.class);

    @Autowired
    JobRepo jobRepo;

    @Autowired
    TeamService teamService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    ServiceHelper serviceHelper;

    public JobDto createJob(CreateJobRequest request) {
        // validate and will throw exception if not exist
        teamService.getTeamWithCompanyIdValidation(request.getCompanyId(), request.getTeamId());

        Job job = Job.builder()
                .name(request.getName())
                .color(request.getColor())
                .teamId(request.getTeamId())
                .build();

        try {
            jobRepo.save(job);
        } catch(Exception ex) {
            String errMsg = "could not create job";
//            serviceHelper.handleErrorAndThrowException(logger, ex, errMsg);
        }

//        LogEntry auditLog = LogEntry.builder()
//                .currentUserId(AuthContext.getUserId())
//                .authorization(AuthContext.getAuthz())
//                .targetType("job")
//                .targetId(job.getId())
//                .companyId(request.getCompanyId())
//                .teamId(job.getTeamId())
//                .updatedContents(job.toString())
//                .build();
//
//        logger.info("created job", auditLog);

        serviceHelper.trackEventAsync("job_created");

        JobDto jobDto = this.convertToDto(job);
        jobDto.setCompanyId(request.getCompanyId());

        return jobDto;
    }

    public JobList listJobs(String companyId, String teamId) {
        // validate and will throw exception if not exist
        teamService.getTeamWithCompanyIdValidation(companyId, teamId);

        JobList jobList = JobList.builder().build();
        List<Job> jobs = jobRepo.findJobByTeamId(teamId);
        for (Job job : jobs) {
            JobDto jobDto = this.convertToDto(job);
            jobDto.setCompanyId(companyId);
            jobList.getJobs().add(jobDto);
        }

        return jobList;
    }

    public JobDto getJob(String jobId, String companyId, String teamId) {
        // validate and will throw exception if not exist
        teamService.getTeamWithCompanyIdValidation(companyId, teamId);

        Job job = jobRepo.findJobById(jobId);
        if (job == null) {
            throw new ServiceException("job not found");
        }

        JobDto jobDto = this.convertToDto(job);
        jobDto.setCompanyId(companyId);

        return jobDto;
    }

    public JobDto updateJob(JobDto jobDtoToUpdate) {
        // validate and will throw exception if not exist
        teamService.getTeamWithCompanyIdValidation(jobDtoToUpdate.getCompanyId(), jobDtoToUpdate.getTeamId());

        JobDto orig = this.getJob(jobDtoToUpdate.getId(), jobDtoToUpdate.getCompanyId(), jobDtoToUpdate.getTeamId());
        Job jobToUpdate = convertToModel(jobDtoToUpdate);

        try {
            jobRepo.save(jobToUpdate);
        } catch (Exception ex) {
            String errMsg = "could not update job";
//            serviceHelper.handleErrorAndThrowException(logger, ex, errMsg);
        }

//        LogEntry auditLog = LogEntry.builder()
//                .currentUserId(AuthContext.getUserId())
//                .authorization(AuthContext.getAuthz())
//                .targetType("job")
//                .targetId(jobDtoToUpdate.getId())
//                .companyId(jobDtoToUpdate.getCompanyId())
//                .teamId(jobDtoToUpdate.getTeamId())
//                .originalContents(orig.toString())
//                .updatedContents(jobDtoToUpdate.toString())
//                .build();
//
//        logger.info("updated job", auditLog);

        serviceHelper.trackEventAsync("job_updated");

        return jobDtoToUpdate;
    }

    JobDto convertToDto(Job job) {
        return modelMapper.map(job, JobDto.class);
    }

    Job convertToModel(JobDto jobDto) {
        return modelMapper.map(jobDto, Job.class);
    }
}
