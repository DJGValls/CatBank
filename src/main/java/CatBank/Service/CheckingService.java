package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.ThirdPartyFactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.DTO.UpadteDatesDTO;
import CatBank.Model.User.AccountHolder;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface CheckingService {

    Checking save(Checking checking);

    List<Checking> checkingsList();

    boolean existsByPrimaryOwner(String primaryOwner);

    boolean existsByAccountHolderId(int accountHolderId);

    boolean existsByCheckingId(int checkingId);


    ResponseEntity deleteChecking(int checkingId, AccountHolder accountHolder);

    Checking checkingFactory(FactoryAccountDTO factoryAccountDTO);

    ResponseEntity allFeeApplycations(int checkingId);

    void penaltyFeeApply(int checkingId);

    Object checkingTransferMoney(TransferenceDTO transferenceDTO);
    ResponseEntity<?> getChecking(AccountHolder accountHolder);
    Checking updateChecking(int checkingId, FactoryAccountDTO factoryAccountDTO);
    ResponseEntity<?> createChecking(FactoryAccountDTO factoryAccountDTO);
    ResponseEntity<?> createCheckingThirdParty(ThirdPartyFactoryAccountDTO thirdPartyFactoryAccountDTO);
    Checking checkingFactoryThirdParty(ThirdPartyFactoryAccountDTO thirdPartyFactoryAccountDTO);
    Checking updateDates(int checkingId, UpadteDatesDTO upadteDatesDTO);


}
