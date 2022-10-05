package CatBank.Model;

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

    private BigDecimal balance;
    @NotNull
    private BigDecimal penaltyFee;

    public AbstractAccount() {

    }

    public AbstractAccount(String primaryOwner, String secundaryOwner, BigDecimal balance) {
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(BigDecimal penaltyFee) {
        this.penaltyFee = penaltyFee;
    }
}
