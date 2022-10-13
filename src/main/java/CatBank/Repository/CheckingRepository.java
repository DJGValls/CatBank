package CatBank.Repository;

import CatBank.Model.Checking;
import CatBank.Model.User.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckingRepository extends JpaRepository<Checking, Integer> {

    boolean existsByPrimaryOwner(String primaryOwner);

    boolean existsByCheckingId(int checkingId);

    Optional<Checking> findByPrimaryOwner(String primaryOwner);




}
