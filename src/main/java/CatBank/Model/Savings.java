package CatBank.Model;

import CatBank.Model.Enums.Status;
import CatBank.Model.User.AccountHolder;
import CatBank.Utils.Money;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "savings")
@EntityListeners(AuditingEntityListener.class)
public class Savings extends AbstractAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int savingsId;

    @NotNull
    private Integer secretKey;

    @Embedded
    @Nullable
    @AttributeOverrides({
            @AttributeOverride( name = "currency", column = @Column(name = "minBalanceCurrencyCode")),
            @AttributeOverride( name = "amount", column = @Column(name = "minBalance"))})
    private Money minBalance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @CreatedDate
    @Nullable
    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Embedded
    @Nullable
    @AttributeOverrides({
            @AttributeOverride( name = "currency", column = @Column(name = "interestRateCurrencyCode")),
            @AttributeOverride( name = "amount", column = @Column(name = "interestRate"))})
    private Money interestRate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "AccountHolder")
    private AccountHolder accountHolder;

    private LocalDate lastMaintenanceAccount;

    public Savings() {
    }

    public Savings(String primaryOwner, String secundaryOwner, Money balance, Integer secretKey, Status status, AccountHolder accountHolder) {
        super(primaryOwner, secundaryOwner, balance);
        this.secretKey = secretKey;
        this.minBalance = new Money(new BigDecimal(1000));
        this.status = status;
        this.creationDate = LocalDate.of(2021, 01, 01);//LocalDate.now();
        this.interestRate = new Money(new BigDecimal(0.1));
        this.accountHolder = accountHolder;
        this.lastMaintenanceAccount = LocalDate.of(2021, 01, 01);//LocalDate.now();
    }

    public int getSavingsId() {
        return savingsId;
    }

    public Integer getSecretKey() {
        return secretKey;
    }

    @Nullable
    public Money getMinBalance() {
        return minBalance;
    }

    public Status getStatus() {
        return status;
    }

    @Nullable
    public LocalDate getCreationDate() {
        return creationDate;
    }

    @Nullable
    public Money getInterestRate() {
        return interestRate;
    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public LocalDate getLastMaintenanceAccount() {
        return lastMaintenanceAccount;
    }

    public void setSecretKey(Integer secretKey) {
        this.secretKey = secretKey;
    }

    public void setMinBalance(Money minBalance) {
        this.minBalance = minBalance;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setInterestRate(Money interestRate) {
        this.interestRate = interestRate;
    }

    public void setAccountHolder(AccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }

    public void setLastMaintenanceAccount(LocalDate lastMaintenanceAccount) {
        this.lastMaintenanceAccount = lastMaintenanceAccount;
    }
}
