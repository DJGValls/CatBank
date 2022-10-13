package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.CreditCard;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
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
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
public class CreditCardServiceImp implements CreditCardService {

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
    public CreditCard save(CreditCard creditCard) {
        return creditCardRepository.save(creditCard);
    }

    @Override
    public List<CreditCard> creditCardList() {
        return creditCardRepository.findAll();
    }

    @Override
    public boolean existsByPrimaryOwner(String primaryOwner) {
        return creditCardRepository.existsByPrimaryOwner(primaryOwner);
    }

    @Override
    public boolean existsByAccountHolderId(int accountHolderId) {
        return creditCardRepository.existsById(accountHolderId);
    }

    @Override
    public ResponseEntity deleteCreditCard(int creditCardId, AccountHolder accountHolder) {
        if (!existsByAccountHolderId(creditCardId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderService.existsByUserName(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("El usuario no existe"), HttpStatus.NOT_FOUND);
        }
        if (!savingsRepository.findById(creditCardId).get().getAccountHolder().getUserName().equals(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("Esa cuenta no le pertenece, no puede borrarla"), HttpStatus.BAD_REQUEST);
        }
        savingsRepository.deleteById(creditCardId);
        return new ResponseEntity(new MensajeDTO("La cuenta ha sido eliminada"), HttpStatus.OK);
    }

    @Override
    public CreditCard creditCardFactory(FactoryAccountDTO factoryAccountDTO) {
        CreditCard StoredCreditCard = new CreditCard(factoryAccountDTO.getPrimaryOwner(),
                factoryAccountDTO.getSecundaryOwner(),
                new Money(new BigDecimal(String.valueOf(factoryAccountDTO.getBalance().getAmount()), new MathContext(6, RoundingMode.HALF_EVEN)),
                        Currency.getInstance(factoryAccountDTO.getBalance().getCurrencyCode())),
                factoryAccountDTO.getAccountHolder());
        return save(StoredCreditCard);
    }

    @Override
    public Object creditCardTransferMoney(TransferenceDTO transferenceDTO) {
        Optional<CreditCard> originAccount = creditCardRepository.findById(transferenceDTO.getOriginId());
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
            savingsRepository.save(savingsDestinyAccount.get());
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
        return creditCardRepository.findByAccountHolderUserName(userName);
    }

    @Override
    public CreditCard updateCreditCard(int creditCardId, FactoryAccountDTO factoryAccountDTOBalance) {
        CreditCard storedCreditCard = creditCardRepository.findById(creditCardId).get();
        storedCreditCard.getBalance().increaseAmount(factoryAccountDTOBalance.getBalance().getAmount());
        return save(storedCreditCard);
    }

    @Override
    public ResponseEntity<?> createCreditCard(FactoryAccountDTO factoryAccountDTO) {
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
        creditCardFactory(factoryAccountDTO);
        return new ResponseEntity<>(new MensajeDTO("La cuenta ha sido creada con éxito"), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getBalance(int creditCardId) {
        Optional<CreditCard> storedCreditCard = creditCardRepository.findById(creditCardId);
        if (storedCreditCard.isPresent()){
            return new ResponseEntity(new MensajeDTO("El saldo actual de su cuenta es de " + storedCreditCard.get().getBalance().getAmount() + "USD"), HttpStatus.OK);
        }return new ResponseEntity(new MensajeDTO("No existe esa cuenta"), HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> getCreditCard(AccountHolder accountHolder) {
        if (!accountHolderService.existsByAccountHolderId(accountHolder.getAccountHolderId())) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderService.existsByUserName(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("El usuario no existe"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderRepository.findById(accountHolder.getAccountHolderId()).get().getUserName().equals(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("Esa cuenta no le pertenece"), HttpStatus.BAD_REQUEST);
        }
        if (!accountHolderRepository.findById(accountHolder.getAccountHolderId()).get().getPassword().equals(accountHolder.getPassword())){
            return new ResponseEntity(new MensajeDTO("Password erroneo"), HttpStatus.BAD_REQUEST);

        } return new ResponseEntity(creditCardRepository.findByPrimaryOwner(accountHolder.getUserName()), HttpStatus.OK);
    }
}
