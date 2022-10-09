package CatBank.Repository;

import CatBank.Model.StudentChecking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentCheckingRepository extends JpaRepository<StudentChecking, Integer> {
    boolean existsByPrimaryOwner(String primaryOwner);

    String findByAccountHolderUserName(String accountHolderUsername);

}
