package tech.staffjoy.account.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tech.staffjoy.account.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findAccountById(String id);

    Optional<Account> findAccountByEmail(String email);

    Optional<Account> findAccountByPhoneNumber(String phoneNumber);

    @Modifying(clearAutomatically = true)
    @Query("update Account account set account.email = :email, account.confirmedAndActive = true where account.id = :id")
    @Transactional
    int updateEmailAndActivateById(@Param("email") String email, @Param("id") String id);

}
