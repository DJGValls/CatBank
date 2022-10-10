package CatBank.Repository;

import CatBank.Model.Savings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsRepository extends JpaRepository<Savings, Integer> {

    boolean existsByPrimaryOwner(String primaryOwner);

    String findByAccountHolderUserName(String accountHolderUserName);


}
