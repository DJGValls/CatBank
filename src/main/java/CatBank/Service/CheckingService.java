package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.DTO.CheckingDTO;
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

    Checking checkingFactory(CheckingDTO checkingDTO);

    ResponseEntity allFeeApplycations(int checkingId);

    void penaltyFeeApply(int checkingId);

    Object transferMoneyBetweenCheckings(TransferenceDTO transferenceDTO);

    String findByAccountHolderUserName(String userName);

    Checking updateChecking(int checkingId, CheckingDTO checkingDTO);

    ResponseEntity<?> createChecking(CheckingDTO checkingDTO);





}
