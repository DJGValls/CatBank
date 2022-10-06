package CatBank.Service;

import CatBank.Model.User.AccountHolder;
import CatBank.Repository.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AccountHolderServiceImp implements AccountHolderService{

    @Autowired
    AccountHolderRepository accountHolderRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public AccountHolder save(AccountHolder accountHolder) {
        return accountHolderRepository.save(accountHolder);
    }

    @Override
    public void delete(int accountHolderId) {
        accountHolderRepository.deleteById(accountHolderId);
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

    @Override
    public boolean existsByDateOfBirthIsBetween(Date dateOfBirth, Calendar instanceDate) {
        return accountHolderRepository.existsByDateOfBirthIsBetween(dateOfBirth,instanceDate);
    }

    @Override
    public AccountHolder accountHolderFactory(AccountHolder accountHolder) {
        AccountHolder accountHolder1 = new AccountHolder(accountHolder.getUserName()
                ,passwordEncoder.encode(accountHolder.getPassword())
                , accountHolder.getDateOfBirth()
                , accountHolder.getAddress()
                , accountHolder.getEmail());
        return accountHolderRepository.save(accountHolder1);

    }


}
