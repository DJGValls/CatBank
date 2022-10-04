package CatBank.Repository;

import CatBank.Model.User.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Integer> {

    Optional <AccountHolder> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByAccountHolderId(Integer accountHolderId);

    boolean existsByUserName(String userName);

}
