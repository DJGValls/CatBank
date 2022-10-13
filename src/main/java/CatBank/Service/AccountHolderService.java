package CatBank.Service;

import CatBank.Model.User.AccountHolder;
import org.springframework.http.ResponseEntity;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AccountHolderService {

    AccountHolder save(AccountHolder accountHolder);

    public void delete(int accountHolderId);

    List<AccountHolder> accountHoldersList();

    Optional<AccountHolder> findByEmail(String email);

    Optional <AccountHolder> findByPassword(String password);

    boolean existByEmail(String email);

    boolean existsByAccountHolderId(Integer accountHolderId);

    boolean existsByUserName(String userName);

    boolean existsByPassword(String password);

    boolean existsByDateOfBirthIsBetween(Date dateOfBirth, Calendar instanceDate);

    AccountHolder accountHolderFactory(AccountHolder accountHolder);


    ResponseEntity<?> createAccountHolder(AccountHolder accountHolder);


}
