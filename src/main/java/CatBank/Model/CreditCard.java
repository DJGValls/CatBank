package CatBank.Model;

import CatBank.Model.Enums.AccountType;
import CatBank.Model.User.AccountHolder;
import CatBank.Utils.Money;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "creditCard")
@EntityListeners(AuditingEntityListener.class)
public class CreditCard extends AbstractAccount{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int creditCardId;

    @Embedded
    @Nullable
    @AttributeOverrides({
            @AttributeOverride( name = "currency", column = @Column(name = "interestRateCurrencyCode")),
            @AttributeOverride( name = "amount", column = @Column(name = "interestRate"))})
    private Money interestRate;

    @Embedded
    @Nullable
    @AttributeOverrides({
            @AttributeOverride( name = "currency", column = @Column(name = "creditLimitCurrencyCode")),
            @AttributeOverride( name = "amount", column = @Column(name = "creditLimit"))})
    private Money creditLimit;

    @CreatedDate
    @Nullable
    @Column(name = "creation_date")
    private LocalDate creationDate;

    @NotNull
    private LocalDate lastMaintenanceAccount;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "AccountHolder")
    private AccountHolder accountHolder;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType accountType;



    public CreditCard() {

    }

    public CreditCard(String primaryOwner, String secundaryOwner, Money balance, AccountHolder accountHolder) {
        super(primaryOwner, secundaryOwner, balance);
        this.interestRate = new Money(new BigDecimal(0.1));
        this.creditLimit = new Money(new BigDecimal(1000));
        this.creationDate = LocalDate.now();
        this.lastMaintenanceAccount = LocalDate.now();
        this.accountHolder = accountHolder;
        this.accountType = AccountType.CREDITCARD;
    }

    public int getCreditCardId() {
        return creditCardId;
    }

    @Nullable
    public Money getInterestRate() {
        return interestRate;
    }

    @Nullable
    public Money getCreditLimit() {
        return creditLimit;
    }

    @Nullable
    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDate getLastMaintenanceAccount() {
        return lastMaintenanceAccount;
    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setInterestRate(@Nullable Money interestRate) {
        this.interestRate = interestRate;
    }

    public void setCreditLimit(@Nullable Money creditLimit) {
        this.creditLimit = creditLimit;
    }

    public void setCreationDate(@Nullable LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastMaintenanceAccount(LocalDate lastMaintenanceAccount) {
        this.lastMaintenanceAccount = lastMaintenanceAccount;
    }

    public void setAccountHolder(AccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
