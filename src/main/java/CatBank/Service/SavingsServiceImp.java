package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.Savings;
import CatBank.Model.StudentChecking;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.CheckingRepository;
import CatBank.Repository.CreditCardRepository;
import CatBank.Repository.SavingsRepository;
import CatBank.Repository.StudentCheckingRepository;
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
public class SavingsServiceImp implements SavingsService{

    @Autowired
    StudentCheckingRepository studentCheckingRepository;
    @Autowired
    AccountHolderService accountHolderService;
    @Autowired
    CheckingRepository checkingRepository;
    @Autowired
    SavingsRepository savingsRepository;
    @Autowired
    CreditCardRepository creditCardRepository;
    @Override
    public Savings save(Savings savings) {
        return savingsRepository.save(savings);
    }

    @Override
    public List<Savings> savingsList() {
        return savingsRepository.findAll();
    }

    @Override
    public boolean existsByPrimaryOwner(String primaryOwner) {
        return savingsRepository.existsByPrimaryOwner(primaryOwner);
    }

    @Override
    public boolean existsByAccountHolderId(int accountHolderId) {
        return savingsRepository.existsById(accountHolderId);
    }

    @Override
    public ResponseEntity deleteSavings(int savingsId, AccountHolder accountHolder) {
        if (!existsByAccountHolderId(savingsId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderService.existsByUserName(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("El usuario no existe"), HttpStatus.NOT_FOUND);
        }
        if (!savingsRepository.findById(savingsId).get().getAccountHolder().getUserName().equals(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("Esa cuenta no le pertenece, no puede borrarla"), HttpStatus.BAD_REQUEST);
        }
        savingsRepository.deleteById(savingsId);
        return new ResponseEntity(new MensajeDTO("La cuenta ha sido eliminada"), HttpStatus.OK);
    }

    @Override
    public Savings savingsFactory(FactoryAccountDTO factoryAccountDTO) {
        Savings StoredSaving = new Savings(factoryAccountDTO.getPrimaryOwner(),
                factoryAccountDTO.getSecundaryOwner(),
                new Money(new BigDecimal(factoryAccountDTO.getBalance().getAmount(), new MathContext(6, RoundingMode.HALF_EVEN)),
                        Currency.getInstance(factoryAccountDTO.getBalance().getCurrencyCode())),
                factoryAccountDTO.getSecretKey(),
                factoryAccountDTO.getStatus(),
                factoryAccountDTO.getAccountHolder());
        return save(StoredSaving);
    }

    @Override
    public ResponseEntity allFeeAndInterestRestApplycations(int savingsId) {
        Optional<Savings> storedSavings = savingsRepository.findById(savingsId);
        if (storedSavings.isPresent()){
            while(LocalDate.now().isAfter(storedSavings.get().getLastMaintenanceAccount().plusMonths(12))){
                storedSavings.get().setLastMaintenanceAccount(storedSavings.get().getLastMaintenanceAccount().plusMonths(12));
                storedSavings.get().setBalance(new Money(storedSavings.get().getBalance().increaseAmount(((storedSavings.get().getInterestRate().getAmount()).multiply(storedSavings.get().getBalance().getAmount()).divide(BigDecimal.valueOf(100))))));

                if (storedSavings.get().getBalance().getAmount().compareTo(storedSavings.get().getMinBalance().getAmount())==-1) {
                    storedSavings.get().getBalance().decreaseAmount(storedSavings.get().getPenaltyFee().getAmount());
                }save(storedSavings.get());
            }save(storedSavings.get());
        }
        return new ResponseEntity(new MensajeDTO("El saldo actual de su cuenta es de " + storedSavings.get().getBalance().getAmount() + "USD"), HttpStatus.OK);
    }

    @Override
    public Object SavingsTransferMoney(TransferenceDTO transferenceDTO) {
        return null;
    }

    @Override
    public String findByAccountHolderUserName(String userName) {
        return null;
    }

    @Override
    public Savings updateSavings(int savingsId, FactoryAccountDTO factoryAccountDTO) {
        return null;
    }

    @Override
    public ResponseEntity<?> createSaving(FactoryAccountDTO factoryAccountDTO) {
        return null;
    }
}
