package CatBank.Repository;

import CatBank.Model.Checking;
import CatBank.Model.User.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingRepository extends JpaRepository<Checking, Integer> {

    boolean existsByPrimaryOwner(String primaryOwner);






}
