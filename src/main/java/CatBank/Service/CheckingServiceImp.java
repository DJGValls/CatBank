package CatBank.Service;


import CatBank.Model.Checking;
import CatBank.Model.DTO.CheckingDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.AccountHolderRepository;
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
    public ResponseEntity allFeeApplycations(int checkingId) {
        Optional<Checking> checkin1 = checkingRepository.findById(checkingId);
        if(checkin1.isPresent()){
            while(LocalDate.now().isAfter(checkin1.get().getLastMaintenanceFee().plusMonths(1))){
                checkin1.get().setLastMaintenanceFee(checkin1.get().getLastMaintenanceFee().plusMonths(1));
                checkin1.get().setBalance(new Money(checkin1.get().getBalance().decreaseAmount(checkin1.get().getMonthlyMaintenanceFee().getAmount())));
                penaltyFeeApply(checkingId);
                checkingRepository.save(checkin1.get());
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
    public Object transferMoneyBetweenCheckings(TransferenceDTO transferenceDTO) {
        Optional<Checking> originAccount = checkingRepository.findById(transferenceDTO.getOriginId());
        Optional<Checking> destinyAccount = checkingRepository.findById(transferenceDTO.getDestinyId());

        if (originAccount.isPresent()
                ||originAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getOriginName())){
            if (destinyAccount.isPresent()||
                    destinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                if (transferenceDTO.getAmount().compareTo(originAccount.get().getBalance().getAmount()) > -1){
                    return new ResponseEntity<>(new MensajeDTO("No puede transferir más dinero del que tiene en su cuenta"), HttpStatus.BAD_REQUEST);
                }else
                originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
                destinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
                checkingRepository.save(originAccount.get());
                checkingRepository.save(destinyAccount.get());
                return new ResponseEntity(new MensajeDTO("el dinero ha sido transferido correctamente, su saldo actual es de " + originAccount.get().getBalance().getAmount() + " USD"), HttpStatus.OK);
            } else return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
        } else return new ResponseEntity(new MensajeDTO("No existe esa cuenta de origen"), HttpStatus.NOT_FOUND);
    }
    @Override
    public String findByAccountHolderUserName(String userName) {
        return checkingRepository.findByAccountHolderUserName(userName);
    }

    @Override
    public Checking updateChecking(int checkingId, CheckingDTO checkingDTOSecundaryOwner) {
        Checking storedChecking = checkingRepository.findById(checkingId).get();
        storedChecking.setSecundaryOwner(checkingDTOSecundaryOwner.getSecundaryOwner());
        return checkingRepository.save(storedChecking);
    }

    @Override
    public ResponseEntity<?> createChecking(CheckingDTO checkingDTO) {
        if (!accountHolderService.existsByAccountHolderId(checkingDTO.getAccountHolder().getAccountHolderId())||
                !accountHolderService.existByEmail(checkingDTO.getAccountHolder().getEmail())||
                !accountHolderService.existsByUserName(checkingDTO.getAccountHolder().getUserName())){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + checkingDTO.getAccountHolder().getUserName() + " no existe, revise si ha sido creado y que su id y sus datos estén correctos"), HttpStatus.BAD_REQUEST);
        }
        if (existsByPrimaryOwner(checkingDTO.getPrimaryOwner())){
            return new ResponseEntity<>(new MensajeDTO("El usuario " + checkingDTO.getAccountHolder().getUserName() + " ya tiene una cuenta Checking creada, revise que los datos sean correctos"), HttpStatus.BAD_REQUEST);
        }
        if (!checkingDTO.getAccountHolder().getUserName().equals(checkingDTO.getPrimaryOwner())){
            return new ResponseEntity<>(new MensajeDTO("El nombre del primaryOwner ha de coincidir con el user name del AccountHolder"), HttpStatus.BAD_REQUEST);
        }
        if (!accountHolderRepository.findById(checkingDTO.getAccountHolder().getAccountHolderId()).get().getUserName().equals(checkingDTO.getAccountHolder().getUserName())){
            return new ResponseEntity<>(new MensajeDTO("El Id de usuario de " + checkingDTO.getPrimaryOwner() + " no es correcto"), HttpStatus.BAD_REQUEST);
        }
        LocalDate start = LocalDate.from(checkingDTO.getAccountHolder().getDateOfBirth());
        LocalDate end = LocalDate.now();
        long years = ChronoUnit.YEARS.between(start, end);
        if(years < 24){

            return studentCheckingService.createStudentChecking(checkingDTO);
        }
        checkingFactory(checkingDTO);
        return new ResponseEntity<>(new MensajeDTO("Cuenta Checking Creada"), HttpStatus.CREATED);
    }


}
