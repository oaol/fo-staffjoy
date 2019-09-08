package tech.staffjoy.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tech.staffjoy.account.model.AccountSecret;

@Repository
public interface AccountSecretRepository extends JpaRepository<AccountSecret, String> {

    Optional<AccountSecret> findAccountSecretByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("update AccountSecret accountSecret set accountSecret.passwordHash = :passwordHash where accountSecret.id = :id")
    @Transactional
    int updatePasswordHashById(@Param("passwordHash") String passwordHash, @Param("id") String id);
}
