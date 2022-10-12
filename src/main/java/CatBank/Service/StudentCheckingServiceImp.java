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
import java.util.Currency;
import java.util.List;
import java.util.Optional;


@Service
public class StudentCheckingServiceImp implements StudentCheckingService{

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
    public StudentChecking save(StudentChecking studentChecking) {
        return studentCheckingRepository.save(studentChecking);
    }

    @Override
    public List<StudentChecking> studentCheckingsList() {
        return studentCheckingRepository.findAll();
    }

    @Override
    public boolean existsByPrimaryOwner(String primaryOwner) {
        return studentCheckingRepository.existsByPrimaryOwner(primaryOwner);
    }

    @Override
    public boolean existsByAccountHolderId(int accountHolderId) {
        return studentCheckingRepository.existsById(accountHolderId);
    }

    @Override
    public ResponseEntity deleteStudentChecking(int studentCheckingId, AccountHolder accountHolder){
        if (!existsByAccountHolderId(studentCheckingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderService.existsByUserName(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("El usuario no existe"), HttpStatus.NOT_FOUND);
        }
        if (!studentCheckingRepository.findById(studentCheckingId).get().getAccountHolder().getUserName().equals(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("Esa cuenta no le pertenece, no puede borrarla"), HttpStatus.BAD_REQUEST);
        }
        studentCheckingRepository.deleteById(studentCheckingId);
        return new ResponseEntity(new MensajeDTO("La cuenta ha sido eliminada"), HttpStatus.OK);
    }


    @Override
    public StudentChecking studentCheckingFactory(FactoryAccountDTO factoryAccountDTO) {
        StudentChecking checking1 = new StudentChecking(factoryAccountDTO.getPrimaryOwner(),
                factoryAccountDTO.getSecundaryOwner(),
                new Money(new BigDecimal(String.valueOf(factoryAccountDTO.getBalance().getAmount()), new MathContext(6, RoundingMode.HALF_EVEN)),
                        Currency.getInstance(factoryAccountDTO.getBalance().getCurrencyCode())),
                factoryAccountDTO.getSecretKey(),
                factoryAccountDTO.getStatus(),
                factoryAccountDTO.getAccountHolder());
        return save(checking1);
    }

    @Override
    public Object studentCheckingTransferMoney(TransferenceDTO transferenceDTO) {
        Optional<StudentChecking> originAccount = studentCheckingRepository.findById(transferenceDTO.getOriginId());
        Optional<Checking> CheckingDestinyAccount = checkingRepository.findById(transferenceDTO.getDestinyId());
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
            if (!CheckingDestinyAccount.isPresent() || !CheckingDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            CheckingDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            studentCheckingRepository.save(originAccount.get());
            checkingRepository.save(CheckingDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.STUDENTCHECKING)){
            if (!studentCheckingDestinyAccount.isPresent() || !studentCheckingDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            studentCheckingDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            studentCheckingRepository.save(originAccount.get());
            studentCheckingRepository.save(studentCheckingDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.SAVINGS)){
            if (!savingsDestinyAccount.isPresent() || !savingsDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            savingsDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            studentCheckingRepository.save(originAccount.get());
            savingsRepository.save(savingsDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.CREDITCARD)){
            if (!creditCardDestinyAccount.isPresent() || !creditCardDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            creditCardDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            studentCheckingRepository.save(originAccount.get());
            creditCardRepository.save(creditCardDestinyAccount.get());
        }
        return new ResponseEntity(new MensajeDTO("el dinero ha sido transferido correctamente, su saldo actual es de " + originAccount.get().getBalance().getAmount() + " USD"), HttpStatus.OK);
    }

    @Override
    public StudentChecking updateStudentChecking(int studentCheckingId, FactoryAccountDTO factoryAccountDTO) {
        StudentChecking storedChecking = studentCheckingRepository.findById(studentCheckingId).get();
        storedChecking.getBalance().increaseAmount(factoryAccountDTO.getBalance().getAmount());
        return save(storedChecking);
    }
    @Override
    public ResponseEntity<?> createStudentChecking(FactoryAccountDTO factoryAccountDTO) {
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
        studentCheckingFactory(factoryAccountDTO);
        return new ResponseEntity<>(new MensajeDTO("El usuario " + factoryAccountDTO.getAccountHolder().getUserName() + " es menor de 24 años, la cuenta ha sido creada como StudentChecking"), HttpStatus.CREATED);
    }
    @Override
    public ResponseEntity<?> getBalance(int studentCheckingId) {
        Optional<StudentChecking> storedStudentChecking = studentCheckingRepository.findById(studentCheckingId);
        if (storedStudentChecking.isPresent()){
            return new ResponseEntity(new MensajeDTO("El saldo actual de su cuenta es de " + storedStudentChecking.get().getBalance().getAmount() + " USD"), HttpStatus.OK);
        }return new ResponseEntity(new MensajeDTO("No existe esa cuenta StudentChecking"), HttpStatus.NOT_FOUND);
    }


}
