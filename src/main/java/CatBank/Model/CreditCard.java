package CatBank.Model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "creditCard")
public class CreditCard extends AbstractAccount{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int creditCardId;

    @NotNull
    private BigDecimal interestRate;

    @NotNull
    private BigDecimal creditLimit;



    public CreditCard() {

    }

}
