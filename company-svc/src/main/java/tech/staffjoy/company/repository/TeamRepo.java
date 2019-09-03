package tech.staffjoy.company.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.staffjoy.company.model.Team;

@Repository
public interface TeamRepo extends JpaRepository<Team, String> {
    List<Team> findByCompanyId(String companyId);
}
