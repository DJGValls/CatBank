package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.DTO.CheckingDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.StudentChecking;
import CatBank.Repository.StudentCheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
public interface StudentCheckingService {

    StudentChecking save(StudentChecking studentChecking);

    List<StudentChecking> studentCheckingsList();

    boolean existsByPrimaryOwner(String primaryOwner);

    boolean existsByAccountHolderId(int accountHolderId);

    void deleteStudentChecking(int accountHolder);

    StudentChecking studentCheckingFactory(CheckingDTO checkingDTO);

    Object transferMoneyBetweenStudentCheckings(TransferenceDTO transferenceDTO);

    StudentChecking updateStudentChecking(int studentCheckingId, CheckingDTO checkingDTO);

    ResponseEntity<?> createStudentChecking(CheckingDTO checkingDTO);


}
