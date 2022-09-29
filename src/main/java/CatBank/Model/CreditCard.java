package CatBank.Model;

import CatBank.Security.Model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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

    @NotNull
    @ManyToMany
    @JoinTable(name = "user_creditCard", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "creditCard_id"))
    private Set<User> creditCardHashset = new HashSet<>();

    public CreditCard() {

    }

    public CreditCard(String primaryOwner, String secundaryOwner, BigDecimal balance, BigDecimal penaltyFee, int creditCardId, BigDecimal interestRate, BigDecimal creditLimit) {
        super(primaryOwner, secundaryOwner, balance, penaltyFee);
        this.creditCardId = creditCardId;
        this.interestRate = interestRate;
        this.creditLimit = creditLimit;
    }

    public int getCreditCardId() {
        return creditCardId;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Set<User> getCreditCardHashset() {
        return creditCardHashset;
    }

    public void setCreditCardHashset(Set<User> creditCardHashset) {
        this.creditCardHashset = creditCardHashset;
    }
}
