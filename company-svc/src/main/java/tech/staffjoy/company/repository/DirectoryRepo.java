package tech.staffjoy.company.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tech.staffjoy.company.model.Directory;

@Repository
public interface DirectoryRepo extends JpaRepository<Directory, String> {

    Directory findByCompanyIdAndUserId(String companyId, String userId);

    Page<Directory> findByCompanyId(String companyId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Query("update Directory directory set directory.internalId = :internalId where directory.companyId = :companyId and directory.userId = :userId")
    @Transactional
    int updateInternalIdByCompanyIdAndUserId(@Param("internalId") String internalId, @Param("companyId") String companyId, @Param("userId") String userId);

}
