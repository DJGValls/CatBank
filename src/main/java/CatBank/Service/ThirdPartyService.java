package CatBank.Service;

import CatBank.Model.User.ThirdParty;
import CatBank.Security.Model.User;

import java.util.List;
import java.util.Optional;

public interface ThirdPartyService {

    ThirdParty save(ThirdParty thirdParty);

    void delete(int thirdPartyId);

    List<ThirdParty> thirdPartysList();

    Optional<ThirdParty> findByUserName(String userName);

    boolean existsByUserName(String userName);

    boolean existsByThirdPartyId(Integer thirdPartyId);

    ThirdParty thirdPartyFactory(ThirdParty thirdParty);

    ThirdParty findByThirdPartyUserName(String userName);

    Optional createThirdParty(ThirdParty thirdParty);

}
