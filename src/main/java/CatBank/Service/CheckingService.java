package CatBank.Service;

import CatBank.Model.Checking;
import CatBank.Model.User.AccountHolder;

import java.util.List;


public interface CheckingService {

    Checking save(Checking checking);

    public List<Checking> checkingsList();

    boolean existsByPrimaryOwner(String primaryOwner);

}
