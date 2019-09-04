package tech.staffjoy.company.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.staffjoy.company.model.Company;

@Repository
public interface CompanyRepo extends JpaRepository<Company, String> {
    Company findCompanyById(String id);
}
