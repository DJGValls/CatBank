package CatBank.Service;

import CatBank.Model.User.AccountHolder;
import CatBank.Repository.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountHolderServiceImp implements AccountHolderService{

    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Override
    public AccountHolder save(AccountHolder accountHolder) {
        return accountHolderRepository.save(accountHolder);
    }

    @Override
    public List<AccountHolder> accountHoldersList() {
        return accountHolderRepository.findAll();
    }
}
