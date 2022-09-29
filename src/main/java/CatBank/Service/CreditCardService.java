package CatBank.Service;

import CatBank.Repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Transactional
public class CreditCardService {

    @Autowired
    CreditCardRepository creditCardRepository;

}
