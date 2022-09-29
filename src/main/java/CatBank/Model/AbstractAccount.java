package CatBank.Model;

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@MappedSuperclass
public abstract class AbstractAccount {



    @NotNull
    private String PrimaryOwner;
    @NotNull
    private String secundaryOwner;
    @NotNull
    private BigDecimal balance;
    @NotNull
    private BigDecimal penaltyFee;

    public AbstractAccount(String primaryOwner, String secundaryOwner, BigDecimal balance, BigDecimal penaltyFee) {
        PrimaryOwner = primaryOwner;
        this.secundaryOwner = secundaryOwner;
        this.balance = balance;
        this.penaltyFee = penaltyFee;
    }

    public AbstractAccount() {
    }

    public String getPrimaryOwner() {
        return PrimaryOwner;
    }

    public void setPrimaryOwner(String primaryOwner) {
        PrimaryOwner = primaryOwner;
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
