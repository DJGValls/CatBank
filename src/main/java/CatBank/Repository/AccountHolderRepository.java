package CatBank.Repository;

import CatBank.Model.User.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Integer> {

    Optional <AccountHolder> findByEmail(String email);

    Optional <AccountHolder> findByPassword(String password);

    boolean existsByEmail(String email);

    boolean existsByPassword(String password);

    boolean existsByAccountHolderId(Integer accountHolderId);

    boolean existsByUserName(String userName);

    Boolean existsByDateOfBirthIsBetween(Date dateOfBirth, Calendar instanceDate);











}
