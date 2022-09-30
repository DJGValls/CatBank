package CatBank.Service;


import CatBank.Model.Checking;
import CatBank.Repository.CheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckingServiceImp implements CheckingService{

    @Autowired
    private CheckingRepository checkingRepository;

    @Override
    public Checking save(Checking checking){
        return checkingRepository.save(checking);
    }

}
