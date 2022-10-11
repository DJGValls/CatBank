package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.Savings;
import CatBank.Model.User.AccountHolder;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface SavingsService {

    Savings save(Savings savings);

    List<Savings> savingsList();

    boolean existsByPrimaryOwner(String primaryOwner);

    boolean existsByAccountHolderId(int accountHolderId);

    ResponseEntity deleteSavings(int savingsId, AccountHolder accountHolder);

    Savings savingsFactory(FactoryAccountDTO factoryAccountDTO);

    Object SavingsTransferMoney(TransferenceDTO transferenceDTO);

    String findByAccountHolderUserName(String userName);

    Savings updateSavings(int savingsId, FactoryAccountDTO factoryAccountDTO);

    ResponseEntity<?> createSaving(FactoryAccountDTO factoryAccountDTO);

    ResponseEntity<?> getBalance(int savingsId);

}
