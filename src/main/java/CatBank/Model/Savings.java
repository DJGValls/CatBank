package CatBank.Model;

import CatBank.Model.Enums.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "savings")
public class Savings extends AbstractAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int savingsId;

    @NotNull
    private String secretKey;

    @NotNull
    private BigDecimal minBalance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private Date creationDate;

    @NotNull
    private BigDecimal interestRate;



    public Savings() {
    }




}
