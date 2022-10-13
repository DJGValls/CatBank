package CatBank.Repository;

import CatBank.Model.Checking;
import CatBank.Model.Savings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Integer> {

    boolean existsByPrimaryOwner(String primaryOwner);

    String findByAccountHolderUserName(String accountHolderUserName);

    Optional<Savings> findByPrimaryOwner(String primaryOwner);
}
