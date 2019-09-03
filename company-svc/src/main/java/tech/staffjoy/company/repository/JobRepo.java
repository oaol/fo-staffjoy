package tech.staffjoy.company.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.staffjoy.company.model.Job;

@Repository
public interface JobRepo extends JpaRepository<Job, String> {
    List<Job> findJobByTeamId(String teamId);
    Job findJobById(String id);
}
