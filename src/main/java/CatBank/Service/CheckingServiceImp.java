package CatBank.Service;


import CatBank.Model.Checking;
import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.DTO.CheckingDTO;
import CatBank.Repository.CheckingRepository;
import CatBank.Utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.List;

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







}
