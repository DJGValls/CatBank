package CatBank.Repository;

import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.ThirdParty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Repository
public interface ThirdPartyRepository extends JpaRepository<ThirdParty, Integer> {

    Optional<ThirdParty> findByUserName(String userName);

    boolean existsByUserName(String userName);

    boolean existsByThirdPartyId(Integer thirdPartyId);



}
