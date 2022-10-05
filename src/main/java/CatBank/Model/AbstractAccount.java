package CatBank.Model;

import CatBank.Utils.Money;
import org.springframework.lang.Nullable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@MappedSuperclass
public abstract class AbstractAccount {


    @NotNull
    private String primaryOwner;
    @NotNull
    private String secundaryOwner;
    @Embedded
    @Nullable
    private Money balance ;// = new Money(new BigDecimal(0));
    @NotNull
    private BigDecimal penaltyFee;

    public AbstractAccount() {
        this.balance = new Money(new BigDecimal(0));
    }

    public AbstractAccount(String primaryOwner, String secundaryOwner, Money balance) {
        this();
        this.primaryOwner = primaryOwner;
        this.secundaryOwner = secundaryOwner;
        this.balance = balance;
        this.penaltyFee = BigDecimal.valueOf(40);
    }

    public String getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(String primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public String getSecundaryOwner() {
        return secundaryOwner;
    }

    public void setSecundaryOwner(String secundaryOwner) {
        this.secundaryOwner = secundaryOwner;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(BigDecimal penaltyFee) {
        this.penaltyFee = penaltyFee;
    }
}
