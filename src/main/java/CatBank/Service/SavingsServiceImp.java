package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.CreditCard;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.Enums.AccountType;
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
        Optional<Savings> originAccount = savingsRepository.findById(transferenceDTO.getOriginId());
        Optional<Checking> checkingDestinyAccount = checkingRepository.findById(transferenceDTO.getDestinyId());
        Optional<StudentChecking> studentCheckingDestinyAccount = studentCheckingRepository.findById(transferenceDTO.getDestinyId());
        Optional<Savings> savingsDestinyAccount = savingsRepository.findById(transferenceDTO.getDestinyId());
        Optional<CreditCard> creditCardDestinyAccount = creditCardRepository.findById(transferenceDTO.getDestinyId());

        if (!originAccount.isPresent() || !originAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getOriginName())){
            return new ResponseEntity(new MensajeDTO("No existe esa cuenta de origen"), HttpStatus.NOT_FOUND);
        }

        if (transferenceDTO.getAmount().compareTo(originAccount.get().getBalance().getAmount()) > -1){
            return new ResponseEntity<>(new MensajeDTO("No puede transferir más dinero del que tiene en su cuenta"), HttpStatus.BAD_REQUEST);
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.CHECKING)){
            if (!checkingDestinyAccount.isPresent() || !checkingDestinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            checkingDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            save(originAccount.get());
            checkingRepository.save(checkingDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.STUDENTCHECKING)){
            if (!studentCheckingDestinyAccount.isPresent() || !studentCheckingDestinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            studentCheckingDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            save(originAccount.get());
            studentCheckingRepository.save(studentCheckingDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.SAVINGS)){
            if (!savingsDestinyAccount.isPresent() || !savingsDestinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            savingsDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            save(originAccount.get());
            save(savingsDestinyAccount.get());
        }
/*        if (transferenceDTO.getDestinyAccountType().equals(AccountType.CREDITCARD)){
            if (!creditCardDestinyAccount.isPresent() || !creditCardDestinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            creditCardDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            save(originAccount.get());
            creditCardRepository.save(creditCardDestinyAccount.get());
        }
*/        return new ResponseEntity(new MensajeDTO("el dinero ha sido transferido correctamente, su saldo actual es de " + originAccount.get().getBalance().getAmount() + " USD"), HttpStatus.OK);
    }

    @Override
    public String findByAccountHolderUserName(String userName) {
        return savingsRepository.findByAccountHolderUserName(userName);
    }

    @Override
    public Savings updateSavings(int savingsId, FactoryAccountDTO factoryAccountDTO) {
        Savings storedSaving = savingsRepository.findById(savingsId).get();
        storedSaving.setSecundaryOwner(factoryAccountDTO.getSecundaryOwner());
        return save(storedSaving);
    }

    @Override
    public ResponseEntity<?> createSaving(FactoryAccountDTO factoryAccountDTO) {
        if (!accountHolderService.existsByAccountHolderId(factoryAccountDTO.getAccountHolder().getAccountHolderId())||
                !accountHolderService.existByEmail(factoryAccountDTO.getAccountHolder().getEmail())||
                !accountHolderService.existsByUserName(factoryAccountDTO.getAccountHolder().getUserName())){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + factoryAccountDTO.getAccountHolder().getUserName() + " no existe, revise si ha sido creado y que su id y sus datos estén correctos"), HttpStatus.BAD_REQUEST);
        }
        if (existsByPrimaryOwner(factoryAccountDTO.getPrimaryOwner())){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + factoryAccountDTO.getAccountHolder().getUserName() + " ya tiene una cuenta Checking creada, revise que los datos sean correctos"), HttpStatus.BAD_REQUEST);
        }
        if (!factoryAccountDTO.getAccountHolder().getUserName().equals(factoryAccountDTO.getPrimaryOwner())){
            return new ResponseEntity<>(new MensajeDTO("El nombre del primaryOwner ha de coincidir con el user name del AccountHolder"), HttpStatus.BAD_REQUEST);
        }
        savingsFactory(factoryAccountDTO);
        return new ResponseEntity<>(new MensajeDTO("La cuenta Savings ha sido creada con éxito"), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getBalance(int savingsId) {
        Optional<Savings> storedSavings = savingsRepository.findById(savingsId);
        if (storedSavings.isPresent()){
            return new ResponseEntity(new MensajeDTO("El saldo actual de su cuenta es de " + storedSavings.get().getBalance().getAmount() + "USD"), HttpStatus.OK);
        }return new ResponseEntity(new MensajeDTO("No existe esa cuenta StudentChecking"), HttpStatus.NOT_FOUND);
    }
}
