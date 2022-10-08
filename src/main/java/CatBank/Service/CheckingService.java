package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.DTO.CheckingDTO;
import CatBank.Model.DTO.TransferenceDTO;

import java.math.BigDecimal;
import java.util.List;


public interface CheckingService {

    Checking save(Checking checking);

    List<Checking> checkingsList();

    boolean existsByPrimaryOwner(String primaryOwner);

    boolean existsByAccountHolderId(int accountHolderId);

    void deleteChecking(int accountHolderId);

    Checking checkingFactory(CheckingDTO checkingDTO);

    BigDecimal allFeeApplycations(int checkingId);

    void penaltyFeeApply(int checkingId);

    Object transferMoneyBetweenCheckings(TransferenceDTO transferenceDTO);

    String findByAccountHolderUserName(String userName);

    Checking updateChecking(int chekingId, CheckingDTO checkingDTO);





}
