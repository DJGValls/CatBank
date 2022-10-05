package CatBank.Service;


import CatBank.Model.Checking;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.CheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckingServiceImp implements CheckingService{

    @Autowired
    private CheckingRepository checkingRepository;

    @Override
    public Checking save(Checking checking){
        return checkingRepository.save(checking);
    }

    @Override
    public List<Checking> checkingsList() {
        return checkingRepository.findAll();
    }

    @Override
    public boolean existsByPrimaryOwner(String primaryOwner) {
        return checkingRepository.existsByPrimaryOwner(primaryOwner);
    }

    @Override
    public boolean existsByAccountHolderId(int accountHolderId) {
        return checkingRepository.existsById(accountHolderId);
    }
    @Override
    public void deleteChecking(int accountHolderId) {
        checkingRepository.deleteById(accountHolderId);

    }


}
