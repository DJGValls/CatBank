package CatBank.Service;


import CatBank.Model.Checking;
import CatBank.Model.CreditCard;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.ThirdPartyFactoryAccountDTO;
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
    @Autowired
    ThirdPartyService thirdPartyService;
    @Autowired
    ThirdPartyRepository thirdPartyRepository;


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
    public boolean existsByCheckingId(int checkingId) {
        return checkingRepository.existsByCheckingId(checkingId);
    }


    @Override
    public ResponseEntity deleteChecking(int checkingId, AccountHolder accountHolder) {
        if (!existsByAccountHolderId(checkingId)) {
            return new ResponseEntity(new MensajeDTO("La cuenta no est?? presente"), HttpStatus.NOT_FOUND);
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
                new Money(new BigDecimal(String.valueOf(factoryAccountDTO.getBalance().getAmount()), new MathContext(6, RoundingMode.HALF_EVEN)),
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
            while(LocalDate.now().isAfter(checkin1.get().getLastMaintenanceAccount().plusMonths(1))){
                checkin1.get().setLastMaintenanceAccount(checkin1.get().getLastMaintenanceAccount().plusMonths(1));
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
            checkingRepository.save(checkin1.get());
            }
        }
    }

    @Override
    public Object checkingTransferMoney(TransferenceDTO transferenceDTO) {
        Optional<Checking> originAccount = checkingRepository.findById(transferenceDTO.getOriginId());
        Optional<Checking> destinyAccount = checkingRepository.findById(transferenceDTO.getDestinyId());
        Optional<StudentChecking> studentCheckingDestinyAccount = studentCheckingRepository.findById(transferenceDTO.getDestinyId());
        Optional<Savings> savingsDestinyAccount = savingsRepository.findById(transferenceDTO.getDestinyId());
        Optional<CreditCard> creditCardDestinyAccount = creditCardRepository.findById(transferenceDTO.getDestinyId());

        if (!originAccount.isPresent() || !originAccount.get().getPrimaryOwner().equals(transferenceDTO.getOriginName())){
            return new ResponseEntity(new MensajeDTO("No existe esa cuenta de origen"), HttpStatus.NOT_FOUND);
        }

        if (transferenceDTO.getAmount().compareTo(originAccount.get().getBalance().getAmount()) > -1){
            return new ResponseEntity<>(new MensajeDTO("No puede transferir m??s dinero del que tiene en su cuenta"), HttpStatus.BAD_REQUEST);
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.CHECKING)){
            if (!destinyAccount.isPresent() || !destinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            destinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            checkingRepository.save(originAccount.get());
            checkingRepository.save(destinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.STUDENTCHECKING)){
            if (!studentCheckingDestinyAccount.isPresent() || !studentCheckingDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            studentCheckingDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            checkingRepository.save(originAccount.get());
            studentCheckingRepository.save(studentCheckingDestinyAccount.get());
        }
        if (transferenceDTO.getDestinyAccountType().equals(AccountType.SAVINGS)){
            if (!savingsDestinyAccount.isPresent() || !savingsDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            savingsDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            checkingRepository.save(originAccount.get());
            savingsRepository.save(savingsDestinyAccount.get());
        }
       if (transferenceDTO.getDestinyAccountType().equals(AccountType.CREDITCARD)){
            if (!creditCardDestinyAccount.isPresent() || !creditCardDestinyAccount.get().getPrimaryOwner().equals(transferenceDTO.getDestinyName())){
                return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
            }
            originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
            creditCardDestinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
            checkingRepository.save(originAccount.get());
            creditCardRepository.save(creditCardDestinyAccount.get());
        }
       return new ResponseEntity(new MensajeDTO("el dinero ha sido transferido correctamente, su saldo actual es de " + originAccount.get().getBalance().getAmount() + " USD"), HttpStatus.OK);
    }

    @Override
    public Checking updateChecking(int checkingId, FactoryAccountDTO factoryAccountDTOBalance) {
        Checking storedChecking = checkingRepository.findById(checkingId).get();
        storedChecking.getBalance().increaseAmount(factoryAccountDTOBalance.getBalance().getAmount());
        return save(storedChecking);
    }
    @Override
    public Checking updateDates(int checkingId, UpadteDatesDTO upadteDatesDTO) {
        Checking stored = checkingRepository.findById(checkingId).get();
        stored.setCreationDate(upadteDatesDTO.getCreationDate());
        stored.setLastMaintenanceAccount(upadteDatesDTO.getLastMaintenanceAccount());
        return save(stored);
    }

    @Override
    public ResponseEntity<?> createChecking(FactoryAccountDTO factoryAccountDTO) {
        if (!accountHolderService.existsByAccountHolderId(factoryAccountDTO.getAccountHolder().getAccountHolderId())||
                !accountHolderService.existByEmail(factoryAccountDTO.getAccountHolder().getEmail())||
                !accountHolderService.existsByUserName(factoryAccountDTO.getAccountHolder().getUserName())){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + factoryAccountDTO.getAccountHolder().getUserName() + " no existe, revise si ha sido creado y que su id y sus datos est??n correctos"), HttpStatus.BAD_REQUEST);
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
        return new ResponseEntity<>(checkingRepository.findByPrimaryOwner(factoryAccountDTO.getPrimaryOwner()), HttpStatus.CREATED);
    }
    @Override
    public ResponseEntity<?> createCheckingThirdParty(ThirdPartyFactoryAccountDTO thirdPartyFactoryAccountDTO) {
        if (!thirdPartyService.existsByThirdPartyId(thirdPartyFactoryAccountDTO.getThirdParty().getThirdPartyId())||
                !thirdPartyService.existsByUserName(thirdPartyFactoryAccountDTO.getThirdParty().getUserName())){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + thirdPartyFactoryAccountDTO.getThirdParty().getUserName() + " no existe, revise si ha sido creado y que su id y sus datos est??n correctos"), HttpStatus.BAD_REQUEST);
        }
        if (existsByPrimaryOwner(thirdPartyFactoryAccountDTO.getPrimaryOwner())){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + thirdPartyFactoryAccountDTO.getThirdParty().getUserName() + " ya tiene una cuenta Checking creada, revise que los datos sean correctos"), HttpStatus.BAD_REQUEST);
        }
        if (!thirdPartyFactoryAccountDTO.getThirdParty().getUserName().equals(thirdPartyFactoryAccountDTO.getPrimaryOwner())){
            return new ResponseEntity<>(new MensajeDTO("El nombre del primaryOwner ha de coincidir con el user name del ThirdParty"), HttpStatus.BAD_REQUEST);
        }
        if (!thirdPartyRepository.findById(thirdPartyFactoryAccountDTO.getThirdParty().getThirdPartyId()).get().getUserName().equals(thirdPartyFactoryAccountDTO.getThirdParty().getUserName())){
            return new ResponseEntity<>(new MensajeDTO("El Id de usuario de " + thirdPartyFactoryAccountDTO.getPrimaryOwner() + " no es correcto"), HttpStatus.BAD_REQUEST);
        }
        checkingFactoryThirdParty(thirdPartyFactoryAccountDTO);
        return new ResponseEntity<>(checkingRepository.findByPrimaryOwner(thirdPartyFactoryAccountDTO.getPrimaryOwner()), HttpStatus.CREATED);
    }

    @Override
    public Checking checkingFactoryThirdParty(ThirdPartyFactoryAccountDTO thirdPartyFactoryAccountDTO) {
        Checking checking1 = new Checking(thirdPartyFactoryAccountDTO.getPrimaryOwner(),
                thirdPartyFactoryAccountDTO.getSecundaryOwner(),
                new Money(new BigDecimal(String.valueOf(thirdPartyFactoryAccountDTO.getBalance().getAmount()), new MathContext(6, RoundingMode.HALF_EVEN)),
                        Currency.getInstance(thirdPartyFactoryAccountDTO.getBalance().getCurrencyCode())),
                thirdPartyFactoryAccountDTO.getSecretKey(),
                thirdPartyFactoryAccountDTO.getThirdParty());
        return save(checking1);
    }

    @Override
    public ResponseEntity<?> getChecking (AccountHolder accountHolder) {

        if (!accountHolderService.existsByAccountHolderId(accountHolder.getAccountHolderId())) {
            return new ResponseEntity(new MensajeDTO("La cuenta no est?? presente"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderService.existsByUserName(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("El usuario no existe"), HttpStatus.NOT_FOUND);
        }
        if (!accountHolderRepository.findById(accountHolder.getAccountHolderId()).get().getUserName().equals(accountHolder.getUserName())){
            return new ResponseEntity(new MensajeDTO("Esa cuenta no le pertenece"), HttpStatus.BAD_REQUEST);
        }
        if (!accountHolderRepository.findById(accountHolder.getAccountHolderId()).get().getEmail().equals(accountHolder.getEmail())){
            return new ResponseEntity(new MensajeDTO("email erroneo"), HttpStatus.BAD_REQUEST);

        } return new ResponseEntity(checkingRepository.findByPrimaryOwner(accountHolder.getUserName()), HttpStatus.OK);
    }

}
