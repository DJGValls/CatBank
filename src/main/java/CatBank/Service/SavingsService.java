package CatBank.Service;

import CatBank.Repository.SavingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Transactional
public class SavingsService {

    @Autowired
    SavingsRepository savingsRepository;

}
