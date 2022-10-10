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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
public class CheckingServiceImp implements CheckingService{

    @Autowired
    CheckingRepository checkingRepository;
    @Autowired
    AccountHolderService accountHolderService;
    @Autowired
    StudentCheckingService studentCheckingService;
    @Autowired
    AccountHolderRepository accountHolderRepository;
    @Autowired
    StudentCheckingRepository studentCheckingRepository;
    @Autowired
    SavingsRepository savingsRepository;
    @Autowired
    CreditCardRepository creditCardRepository;


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
    public ResponseEntity deleteChecking(int checkingId, AccountHolder accountHolder) {
        if (!existsByAccountHolderId(checkingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no está presente"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderService.existsByUserName(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("El usuario no existe"), HttpStatus.NOT_FOUND);
        }
        if (!checkingRepository.findById(checkingId).get().getAccountHolder().getUserName().equals(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("Esa cuenta no le pertenece, no puede borrarla"), HttpStatus.BAD_REQUEST);
        }
        checkingRepository.deleteById(checkingId);
        return new ResponseEntity(new MensajeDTO("La cuenta Checking ha sido eliminada"), HttpStatus.OK);
    }

    @Override
    public Checking checkingFactory(FactoryAccountDTO factoryAccountDTO) {
        Checking checking1 = new Checking(factoryAccountDTO.getPrimaryOwner(),
                factoryAccountDTO.getSecundaryOwner(),
                new Money(new BigDecimal(factoryAccountDTO.getBalance().getAmount(), new MathContext(6, RoundingMode.HALF_EVEN)),
                        Currency.getInstance(factoryAccountDTO.getBalance().getCurrencyCode())),
                factoryAccountDTO.getSecretKey(),
                factoryAccountDTO.getStatus(),
                factoryAccountDTO.getAccountHolder());
        return save(checking1);
    }

    @Override
    public ResponseEntity allFeeApplycations(int checkingId) {
        Optional<Checking> checkin1 = checkingRepository.findById(checkingId);
        if(checkin1.isPresent()){
            while(LocalDate.now().isAfter(checkin1.get().getLastMaintenanceFee().plusMonths(1))){
                checkin1.get().setLastMaintenanceFee(checkin1.get().getLastMaintenanceFee().plusMonths(1));
                checkin1.get().setBalance(new Money(checkin1.get().getBalance().decreaseAmount(checkin1.get().getMonthlyMaintenanceFee().getAmount())));
                penaltyFeeApply(checkingId);
                save(checkin1.get());
            } return new ResponseEntity(new MensajeDTO("El saldo actual de su cuenta es de " + checkin1.get().getBalance().getAmount() + "USD"), HttpStatus.OK);
        } return new ResponseEntity(new MensajeDTO("No existe esa cuenta checking"), HttpStatus.NOT_FOUND);

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

    @Override
    public Object checkingTransferMoney(TransferenceDTO transferenceDTO) {
        Optional<Checking> originAccount = checkingRepository.findById(transferenceDTO.getOriginId());
        Optional<Checking> destinyAccount = checkingRepository.findById(transferenceDTO.getDestinyId());
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
            if (!destinyAccount.isPresent() || !destinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            destinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            checkingRepository.save(originAccount.get());
            checkingRepository.save(destinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.STUDENTCHECKING)){
            if (!studentCheckingDestinyAccount.isPresent() || !studentCheckingDestinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            studentCheckingDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            checkingRepository.save(originAccount.get());
            studentCheckingRepository.save(studentCheckingDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.SAVINGS)){
            if (!savingsDestinyAccount.isPresent() || !savingsDestinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            savingsDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            checkingRepository.save(originAccount.get());
            savingsRepository.save(savingsDestinyAccount.get());
        }
/*        if (transferenceDTO.getDestinyAccountType().equals(AccountType.CREDITCARD)){
            if (!creditCardDestinyAccount.isPresent() || !creditCardDestinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            creditCardDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            checkingRepository.save(originAccount.get());
            creditCardRepository.save(creditCardDestinyAccount.get());
        }
*/        return new ResponseEntity(new MensajeDTO("el dinero ha sido transferido correctamente, su saldo actual es de " + originAccount.get().getBalance().getAmount() + " USD"), HttpStatus.OK);
    }
    @Override
    public String findByAccountHolderUserName(String userName) {
        return checkingRepository.findByAccountHolderUserName(userName);
    }

    @Override
    public Checking updateChecking(int checkingId, FactoryAccountDTO factoryAccountDTOSecundaryOwner) {
        Checking storedChecking = checkingRepository.findById(checkingId).get();
        storedChecking.setSecundaryOwner(factoryAccountDTOSecundaryOwner.getSecundaryOwner());
        return save(storedChecking);
    }

    @Override
    public ResponseEntity<?> createChecking(FactoryAccountDTO factoryAccountDTO) {
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
        if (!accountHolderRepository.findById(factoryAccountDTO.getAccountHolder().getAccountHolderId()).get().getUserName().equals(factoryAccountDTO.getAccountHolder().getUserName())){
            return new ResponseEntity<>(new MensajeDTO("El Id de usuario de " + factoryAccountDTO.getPrimaryOwner() + " no es correcto"), HttpStatus.BAD_REQUEST);
        }
        LocalDate start = LocalDate.from(factoryAccountDTO.getAccountHolder().getDateOfBirth());
        LocalDate end = LocalDate.now();
        long years = ChronoUnit.YEARS.between(start, end);
        if(years < 24){

            return studentCheckingService.createStudentChecking(factoryAccountDTO);
        }
        checkingFactory(factoryAccountDTO);
        return new ResponseEntity<>(new MensajeDTO("Cuenta Checking Creada"), HttpStatus.CREATED);
    }


}
