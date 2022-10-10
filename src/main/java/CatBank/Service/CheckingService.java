package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.User.AccountHolder;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface CheckingService {

    Checking save(Checking checking);

    List<Checking> checkingsList();

    boolean existsByPrimaryOwner(String primaryOwner);

    boolean existsByAccountHolderId(int accountHolderId);

    ResponseEntity deleteChecking(int checkingId, AccountHolder accountHolder);

    Checking checkingFactory(FactoryAccountDTO factoryAccountDTO);

    ResponseEntity allFeeApplycations(int checkingId);

    void penaltyFeeApply(int checkingId);

    Object checkingTransferMoney(TransferenceDTO transferenceDTO);

    String findByAccountHolderUserName(String userName);

    Checking updateChecking(int checkingId, FactoryAccountDTO factoryAccountDTO);

    ResponseEntity<?> createChecking(FactoryAccountDTO factoryAccountDTO);





}
