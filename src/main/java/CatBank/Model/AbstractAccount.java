package CatBank.Model;

import CatBank.Utils.Money;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@MappedSuperclass
public abstract class AbstractAccount {


    @NotNull
    private String primaryOwner;
    @Nullable
    private String secundaryOwner;
    @Embedded
    @Nullable
    @AttributeOverrides({
            @AttributeOverride( name = "currency", column = @Column(name = "balance_currency_code")),
            @AttributeOverride( name = "amount", column = @Column(name = "balance"))})
    private Money balance ;
    @Embedded
    @Nullable
    @AttributeOverrides({
            @AttributeOverride( name = "currency", column = @Column(name = "penaltyFee_currency_code")),
            @AttributeOverride( name = "amount", column = @Column(name = "penaltyFee"))})
    private Money penaltyFee;

    public AbstractAccount() {
        this.balance = new Money(new BigDecimal(0));
    }

    public AbstractAccount(String primaryOwner, String secundaryOwner, Money balance) {
        this();
        this.primaryOwner = primaryOwner;
        this.secundaryOwner = secundaryOwner;
        this.balance = balance;
        this.penaltyFee = new Money(new BigDecimal(40));
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

    public Money getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(Money penaltyFee) {
        this.penaltyFee = penaltyFee;
    }
}
