package CatBank.Service;


import CatBank.Model.Checking;
import CatBank.Model.User.DTO.CheckingDTO;
import CatBank.Repository.CheckingRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
public class CheckingServiceImp implements CheckingService{

    @Autowired
    private CheckingRepository checkingRepository;


    @Override
    public Checking save(Checking checking){
        return checkingRepository.save(checking);
    }

    @Override
    public List<Checking> checkingsList() {
        return checkingRepository.findAll();
    }

    @Override
    public boolean existsByPrimaryOwner(String primaryOwner) {
        return checkingRepository.existsByPrimaryOwner(primaryOwner);
    }

    @Override
    public boolean existsByAccountHolderId(int accountHolderId) {
        return checkingRepository.existsById(accountHolderId);
    }
    @Override
    public void deleteChecking(int accountHolderId) {
        checkingRepository.deleteById(accountHolderId);

    }

    @Override
    public Checking checkingFactory(CheckingDTO checkingDTO) {
        Checking checking1 = new Checking(checkingDTO.getPrimaryOwner(),
                checkingDTO.getSecundaryOwner(),
                new Money(new BigDecimal(checkingDTO.getBalance().getAmount(), new MathContext(6, RoundingMode.HALF_EVEN)),
                        Currency.getInstance(checkingDTO.getBalance().getCurrencyCode())),
                checkingDTO.getSecretKey(),
                checkingDTO.getStatus(),
                checkingDTO.getAccountHolder());
        return checkingRepository.save(checking1);
    }

    @Override
    public void feeApplycations(int checkingId) {
        Optional<Checking> checkin1 = checkingRepository.findById(checkingId);
        if(checkin1.isPresent()){
            while(LocalDate.now().isAfter(checkin1.get().getLastMaintenanceFee().plusMonths(1))){
                checkin1.get().setLastMaintenanceFee(checkin1.get().getLastMaintenanceFee().plusMonths(1));
                checkin1.get().setBalance(new Money(checkin1.get().getBalance().decreaseAmount(checkin1.get().getMonthlyMaintenanceFee().getAmount())));
                penaltyFeeApply(checkingId);
                checkingRepository.save(checkin1.get());
            }
        } new ResponseEntity(new MensajeDTO("No existe esa cuenta checking"), HttpStatus.NOT_FOUND);
        checkin1.get().getBalance().getAmount();
    }

    @Override
    public void penaltyFeeApply(int checkingId) {
        Optional<Checking> checkin1 = checkingRepository.findById(checkingId);
        if(checkin1.isPresent()){
            if (checkin1.get().getBalance().getAmount().compareTo(checkin1.get().getMinBalance().getAmount())==-1){
            checkin1.get().getBalance().decreaseAmount(checkin1.get().getPenaltyFee().getAmount());
            }
        } new ResponseEntity(new MensajeDTO("No existe esa cuenta checking"), HttpStatus.NOT_FOUND);
    }


}
