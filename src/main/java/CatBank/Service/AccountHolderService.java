package CatBank.Service;

import CatBank.Model.User.AccountHolder;

import java.util.List;
import java.util.Optional;

public interface AccountHolderService {

    AccountHolder save(AccountHolder accountHolder);

    List<AccountHolder> accountHoldersList();

    Optional<AccountHolder> findByEmail(String email);

    boolean existByEmail(String email);

    boolean existsByAccountHolderId(Integer accountHolderId);

    boolean existsByUserName(String userName);


}
