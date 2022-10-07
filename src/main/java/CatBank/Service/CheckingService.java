package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.User.DTO.CheckingDTO;

import java.math.BigDecimal;
import java.util.List;


public interface CheckingService {

    Checking save(Checking checking);

    List<Checking> checkingsList();

    boolean existsByPrimaryOwner(String primaryOwner);

    boolean existsByAccountHolderId(int accountHolderId);

    void deleteChecking(int accountHolderId);

    Checking checkingFactory(CheckingDTO checkingDTO);

    BigDecimal getBalance(int checkingId);

    public void penaltyFeeApply(int checkingId);



}
