package CatBank.Service;

import CatBank.Model.User.AccountHolder;
import CatBank.Repository.AccountHolderRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.User;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountHolderServiceImp implements AccountHolderService{

    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;


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

    @Override
    public AccountHolder findByAccountHolderUserName(String userName) {
        return accountHolderRepository.findByUserName(userName);
    }

    @Override
    public ResponseEntity<?> createAccountHolder(AccountHolder accountHolder) {
        if (userService.existsByUserName(accountHolder.getUserName())) {
            return new ResponseEntity<>(new MensajeDTO(accountHolder.getUserName() + " está en uso, cambie su nombre de usario"), HttpStatus.BAD_REQUEST);
        }
        if (existByEmail(accountHolder.getEmail())){
            return new ResponseEntity<>(new MensajeDTO(accountHolder.getEmail() + " ya está en uso"), HttpStatus.BAD_REQUEST);
        }
        if (!accountHolder.getEmail().matches("^(.+)@(\\S+)$")){
            return new ResponseEntity<>(new MensajeDTO(" el formato de Email debería ser xxx@yyy.zzz"), HttpStatus.BAD_REQUEST);
        }
        User user = new User(accountHolder.getUserName(), passwordEncoder.encode(accountHolder.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_ACCOUNTHOLDER).get()));
        if (accountHolder.getUserName().contains("admin"))
            return new ResponseEntity<>(new MensajeDTO("nombre de usuario no puede contener la palabra admin, pruebe una vez más"), HttpStatus.BAD_REQUEST);
        user.setRoles(roles);
        userService.save(user);
        accountHolderFactory(accountHolder);
        return new ResponseEntity(new MensajeDTO("Usuario creado"), HttpStatus.OK);
    }

}
