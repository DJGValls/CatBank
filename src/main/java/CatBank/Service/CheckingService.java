package CatBank.Service;

import CatBank.Model.Checking;

import java.util.List;


public interface CheckingService {

    Checking save(Checking checking);

    public List<Checking> checkingsList();

}
