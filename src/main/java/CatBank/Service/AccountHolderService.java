package CatBank.Service;

import CatBank.Model.User.AccountHolder;

import java.util.List;

public interface AccountHolderService {

    AccountHolder save(AccountHolder accountHolder);

    public List<AccountHolder> accountHoldersList();

}
