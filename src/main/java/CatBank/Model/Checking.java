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

@Entity
@Table(name = "checking")
@EntityListeners(AuditingEntityListener.class)
public class Checking extends AbstractAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int checkingId;

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

    //@Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @Nullable
    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Embedded
    @Nullable
    @AttributeOverrides({
            @AttributeOverride( name = "currency", column = @Column(name = "monthlyMaintenanceFeeCurrencyCode")),
            @AttributeOverride( name = "amount", column = @Column(name = "monthlyMaintenanceFee"))})
    private Money monthlyMaintenanceFee;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "AccountHolder")
    private AccountHolder accountHolder;

    private LocalDate lastMaintenanceFee;


    public Checking() {
        super();
    }

    public Checking(String primaryOwner, String secundaryOwner, Money balance, Integer secretKey, Status status, AccountHolder accountHolder) {
        super(primaryOwner, secundaryOwner, balance);
        this.secretKey = secretKey;
        this.minBalance = new Money(new BigDecimal(250));
        this.status = status;
        this.creationDate = LocalDate.now();
        this.monthlyMaintenanceFee = new Money(new BigDecimal(12));
        this.accountHolder = accountHolder;
        this.lastMaintenanceFee = LocalDate.now();
    }
    public int getCheckingId() {
        return checkingId;
    }

    public Integer getSecretKey() {
        return secretKey;
    }

    public Money getMinBalance() {
        return minBalance;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Money getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
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

    public void setMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public void setAccountHolder(AccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }

    public LocalDate getLastMaintenanceFee() {
        return lastMaintenanceFee;
    }

    public void setLastMaintenanceFee(LocalDate lastMaintenanceFee) {
        this.lastMaintenanceFee = lastMaintenanceFee;
    }
}
