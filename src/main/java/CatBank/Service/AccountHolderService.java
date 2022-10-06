package CatBank.Service;

import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.DTO.CheckingDTO;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AccountHolderService {

    AccountHolder save(AccountHolder accountHolder);

    public void delete(int accountHolderId);

    List<AccountHolder> accountHoldersList();

    Optional<AccountHolder> findByEmail(String email);

    boolean existByEmail(String email);

    boolean existsByAccountHolderId(Integer accountHolderId);

    boolean existsByUserName(String userName);

    boolean existsByDateOfBirthIsBetween(Date dateOfBirth, Calendar instanceDate);

    AccountHolder accountHolderFactory(AccountHolder accountHolder);


}
