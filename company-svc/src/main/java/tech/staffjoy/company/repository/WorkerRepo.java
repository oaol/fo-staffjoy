package tech.staffjoy.company.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tech.staffjoy.company.model.Worker;

@Repository
public interface WorkerRepo extends JpaRepository<Worker, String> {
    List<Worker> findByTeamId(String teamId);
    List<Worker> findByUserId(String userId);
    Worker findByTeamIdAndUserId(String teamId, String userId);

    @Modifying(clearAutomatically = true)
    @Query("delete from Worker worker where worker.teamId = :teamId and worker.userId = :userId")
    @Transactional
    int deleteWorker(@Param("teamId") String teamId, @Param("userId") String userId);
}
