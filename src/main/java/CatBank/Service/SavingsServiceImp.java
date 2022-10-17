package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.CreditCard;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.DTO.UpadteDatesDTO;
import CatBank.Model.Enums.AccountType;
import CatBank.Model.Savings;
import CatBank.Model.StudentChecking;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.*;
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
    @Autowired
    AccountHolderRepository accountHolderRepository;

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
                new Money(new BigDecimal(String.valueOf(factoryAccountDTO.getBalance().getAmount()), new MathContext(6, RoundingMode.HALF_EVEN)),
                        Currency.getInstance(factoryAccountDTO.getBalance().getCurrencyCode())),
                factoryAccountDTO.getSecretKey(),
                factoryAccountDTO.getStatus(),
                factoryAccountDTO.getAccountHolder());
        return save(StoredSaving);
    }



    @Override
    public Object SavingsTransferMoney(TransferenceDTO transferenceDTO) {
        Optional<Savings> originAccount = savingsRepository.findById(transferenceDTO.getOriginId());
        Optional<Checking> checkingDestinyAccount = checkingRepository.findById(transferenceDTO.getDestinyId());
        Optional<StudentChecking> studentCheckingDestinyAccount = studentCheckingRepository.findById(transferenceDTO.getDestinyId());
        Optional<Savings> savingsDestinyAccount = savingsRepository.findById(transferenceDTO.getDestinyId());
        Optional<CreditCard> creditCardDestinyAccount = creditCardRepository.findById(transferenceDTO.getDestinyId());

        if (!originAccount.isPresent() || !originAccount.get().getPrimaryOwner().equals(transferenceDTO.getOriginName())){
            return new ResponseEntity(new MensajeDTO("No existe esa cuenta de origen"), HttpStatus.NOT_FOUND);
        }

        if (transferenceDTO.getAmount().compareTo(originAccount.get().getBalance().getAmount()) > -1){
            return new ResponseEntity<>(new MensajeDTO("No puede transferir más dinero del que tiene en su cuenta"), HttpStatus.BAD_REQUEST);
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.CHECKING)){
            if (!checkingDestinyAccount.isPresent() || !checkingDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            checkingDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            save(originAccount.get());
            checkingRepository.save(checkingDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.STUDENTCHECKING)){
            if (!studentCheckingDestinyAccount.isPresent() || !studentCheckingDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            studentCheckingDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            save(originAccount.get());
            studentCheckingRepository.save(studentCheckingDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.SAVINGS)){
            if (!savingsDestinyAccount.isPresent() || !savingsDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            savingsDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            save(originAccount.get());
            save(savingsDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.CREDITCARD)){
            if (!creditCardDestinyAccount.isPresent() || !creditCardDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            creditCardDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            save(originAccount.get());
            creditCardRepository.save(creditCardDestinyAccount.get());
        }
        return new ResponseEntity(new MensajeDTO("el dinero ha sido transferido correctamente, su saldo actual es de " + originAccount.get().getBalance().getAmount() + " USD"), HttpStatus.OK);
    }

    @Override
    public String findByAccountHolderUserName(String userName) {
        return savingsRepository.findByAccountHolderUserName(userName);
    }

    @Override
    public Savings updateSavings(int savingsId, FactoryAccountDTO factoryAccountDTO) {
        Savings storedSaving = savingsRepository.findById(savingsId).get();
        storedSaving.getBalance().increaseAmount(factoryAccountDTO.getBalance().getAmount());
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
            return new ResponseEntity<>(new MensajeDTO("El usuario " + factoryAccountDTO.getAccountHolder().getUserName() + " ya tiene una cuenta creada, revise que los datos sean correctos"), HttpStatus.BAD_REQUEST);
        }
        if (!factoryAccountDTO.getAccountHolder().getUserName().equals(factoryAccountDTO.getPrimaryOwner())){
            return new ResponseEntity<>(new MensajeDTO("El nombre del primaryOwner ha de coincidir con el user name del AccountHolder"), HttpStatus.BAD_REQUEST);
        }
        savingsFactory(factoryAccountDTO);
        return new ResponseEntity<>(savingsRepository.findByPrimaryOwner(factoryAccountDTO.getPrimaryOwner()), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getBalance(int savingsId) {
        Optional<Savings> storedSavings = savingsRepository.findById(savingsId);
        if (storedSavings.isPresent()){
            return new ResponseEntity(new MensajeDTO("El saldo actual de su cuenta es de " + storedSavings.get().getBalance().getAmount() + "USD"), HttpStatus.OK);
        }return new ResponseEntity(new MensajeDTO("No existe esa cuenta"), HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> getSavings(AccountHolder accountHolder) {
        if (!accountHolderService.existsByAccountHolderId(accountHolder.getAccountHolderId())) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderService.existsByUserName(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("El usuario no existe"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderRepository.findById(accountHolder.getAccountHolderId()).get().getUserName().equals(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("Esa cuenta no le pertenece"), HttpStatus.BAD_REQUEST);
        }
        if (!accountHolderRepository.findById(accountHolder.getAccountHolderId()).get().getEmail().equals(accountHolder.getEmail())){
            return new ResponseEntity(new MensajeDTO("email erroneo"), HttpStatus.BAD_REQUEST);

        } return new ResponseEntity(savingsRepository.findByPrimaryOwner(accountHolder.getUserName()), HttpStatus.OK);
    }

    @Override
    public Savings updateDates(int savingsId, UpadteDatesDTO upadteDatesDTO) {
        Savings stored = savingsRepository.findById(savingsId).get();
        stored.setCreationDate(upadteDatesDTO.getCreationDate());
        stored.setLastMaintenanceAccount(upadteDatesDTO.getLastMaintenanceAccount());
        return save(stored);
    }
}
