package CatBank.Service;

import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.StudentChecking;
import CatBank.Model.User.AccountHolder;
import org.springframework.http.ResponseEntity;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
public interface StudentCheckingService {

    StudentChecking save(StudentChecking studentChecking);

    List<StudentChecking> studentCheckingsList();

    boolean existsByPrimaryOwner(String primaryOwner);

    boolean existsByAccountHolderId(int accountHolderId);

    ResponseEntity deleteStudentChecking(int studentCheckingId, AccountHolder accountHolder);

    StudentChecking studentCheckingFactory(FactoryAccountDTO factoryAccountDTO);

    Object studentCheckingTransferMoney(TransferenceDTO transferenceDTO);

    StudentChecking updateStudentChecking(int studentCheckingId, FactoryAccountDTO factoryAccountDTO);

    ResponseEntity<?> createStudentChecking(FactoryAccountDTO factoryAccountDTO);

    ResponseEntity<?> getBalance(int studentCheckingId);


}
