package CatBank.Service;

import CatBank.Repository.CheckingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Transactional
public class CheckingService {

    @Autowired
    CheckingRepository checkingRepository;

}
