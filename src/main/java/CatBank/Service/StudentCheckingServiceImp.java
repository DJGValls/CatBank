package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.DTO.CheckingDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.StudentChecking;
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
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.List;
import java.util.Optional;


@Service
public class StudentCheckingServiceImp implements StudentCheckingService{

    @Autowired
    StudentCheckingRepository studentCheckingRepository;
    @Autowired
    AccountHolderService accountHolderService;

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
    public void deleteStudentChecking(int accountHolderId) {
        studentCheckingRepository.deleteById(accountHolderId);
    }

    @Override
    public StudentChecking studentCheckingFactory(CheckingDTO checkingDTO) {
        StudentChecking checking1 = new StudentChecking(checkingDTO.getPrimaryOwner(),
                checkingDTO.getSecundaryOwner(),
                new Money(new BigDecimal(checkingDTO.getBalance().getAmount(), new MathContext(6, RoundingMode.HALF_EVEN)),
                        Currency.getInstance(checkingDTO.getBalance().getCurrencyCode())),
                checkingDTO.getSecretKey(),
                checkingDTO.getStatus(),
                checkingDTO.getAccountHolder());
        return studentCheckingRepository.save(checking1);

    }

    @Override
    public Object transferMoneyBetweenStudentCheckings(TransferenceDTO transferenceDTO) {
        Optional<StudentChecking> originAccount = studentCheckingRepository.findById(transferenceDTO.getOriginId());
        Optional<StudentChecking> destinyAccount = studentCheckingRepository.findById(transferenceDTO.getDestinyId());
        if (originAccount.isPresent()
                ||originAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getOriginName())){
            if (destinyAccount.isPresent()||
                    destinyAccount.get().getAccountHolder().getUserName().equals(transferenceDTO.getDestinyName())){
                if (transferenceDTO.getAmount().compareTo(originAccount.get().getBalance().getAmount()) > -1){
                    return new ResponseEntity<>(new MensajeDTO("No puede transferir más dinero del que tiene en su cuenta"), HttpStatus.BAD_REQUEST);
                }else
                    originAccount.get().getBalance().decreaseAmount(transferenceDTO.getAmount());
                destinyAccount.get().getBalance().increaseAmount(transferenceDTO.getAmount());
                studentCheckingRepository.save(originAccount.get());
                studentCheckingRepository.save(destinyAccount.get());
                return new ResponseEntity(new MensajeDTO("el dinero ha sido transferido correctamente, su saldo actual es de " + originAccount.get().getBalance().getAmount() + " USD"), HttpStatus.OK);
            } else return new ResponseEntity(new MensajeDTO("No existe esa cuenta de destino"), HttpStatus.NOT_FOUND);
        } else return new ResponseEntity(new MensajeDTO("No existe esa cuenta de origen"), HttpStatus.NOT_FOUND);
    }

    @Override
    public StudentChecking updateStudentChecking(int studentCheckingId, CheckingDTO checkingDTO) {
        StudentChecking storedChecking = studentCheckingRepository.findById(studentCheckingId).get();
        storedChecking.setSecundaryOwner(checkingDTO.getSecundaryOwner());
        return studentCheckingRepository.save(storedChecking);
    }

    @Override
    public ResponseEntity<?> createStudentChecking(CheckingDTO checkingDTO) {
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
        studentCheckingFactory(checkingDTO);
        return new ResponseEntity<>(new MensajeDTO("El usuario " + checkingDTO.getAccountHolder().getUserName() + " es menor de 24 años, la cuenta ha sido creada como StudentChecking"), HttpStatus.CREATED);
    }


}
