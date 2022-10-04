package CatBank.Service;

import CatBank.Model.User.AccountHolder;
import CatBank.Repository.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<AccountHolder> findByEmail(String email) {
        return accountHolderRepository.findByEmail(email);
    }

    @Override
    public boolean existByEmail(String email) {
        return accountHolderRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByAccountHolderId(Integer accountHolderId) {
        return accountHolderRepository.existsByAccountHolderId(accountHolderId);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return accountHolderRepository.existsByUserName(userName);
    }


}
