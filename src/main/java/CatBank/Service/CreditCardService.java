package CatBank.Service;

import CatBank.Model.CreditCard;
import CatBank.Model.DTO.FactoryAccountDTO;
import CatBank.Model.DTO.TransferenceDTO;
import CatBank.Model.Savings;
import CatBank.Model.User.AccountHolder;
import CatBank.Repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Transactional
public interface CreditCardService {

    CreditCard save(CreditCard creditCard);

    List<CreditCard> creditCardList();

    boolean existsByPrimaryOwner(String primaryOwner);

    boolean existsByAccountHolderId(int accountHolderId);

    ResponseEntity deleteCreditCard(int creditCardId, AccountHolder accountHolder);

    CreditCard creditCardFactory(FactoryAccountDTO factoryAccountDTO);

    Object creditCardTransferMoney(TransferenceDTO transferenceDTO);

    String findByAccountHolderUserName(String userName);

    CreditCard updateCreditCard(int creditCard, FactoryAccountDTO factoryAccountDTO);

    ResponseEntity<?> createCreditCard(FactoryAccountDTO factoryAccountDTO);

    ResponseEntity<?> getBalance(int creditCardId);

    ResponseEntity<?> getCreditCard(AccountHolder accountHolder);


}
