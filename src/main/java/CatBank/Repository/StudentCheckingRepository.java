package CatBank.Repository;

import CatBank.Model.Checking;
import CatBank.Model.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentCheckingRepository extends JpaRepository<StudentChecking, Integer> {
    boolean existsByPrimaryOwner(String primaryOwner);

    String findByAccountHolderUserName(String accountHolderUsername);

    Optional<StudentChecking> findByPrimaryOwner(String primaryOwner);
}
