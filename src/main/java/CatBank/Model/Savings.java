package CatBank.Model;

import CatBank.Model.Enums.Status;
import CatBank.Security.Model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @NotNull
    @ManyToMany
    @JoinTable(name = "user_savings", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "savings_id"))
    private Set<User> savingsHashset = new HashSet<>();

    public Savings() {
    }

    public Savings(String primaryOwner, String secundaryOwner, BigDecimal balance, BigDecimal penaltyFee, int savingsId, String secretKey, BigDecimal minBalance, Status status, Date creationDate, BigDecimal interestRate) {
        super(primaryOwner, secundaryOwner, balance, penaltyFee);
        this.savingsId = savingsId;
        this.secretKey = secretKey;
        this.minBalance = minBalance;
        this.status = status;
        this.creationDate = creationDate;
        this.interestRate = interestRate;
    }

    public int getSavingsId() {
        return savingsId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public BigDecimal getMinBalance() {
        return minBalance;
    }

    public Status getStatus() {
        return status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setMinBalance(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public Set<User> getSavingsHashset() {
        return savingsHashset;
    }

    public void setSavingsHashset(Set<User> savingsHashset) {
        this.savingsHashset = savingsHashset;
    }
}
