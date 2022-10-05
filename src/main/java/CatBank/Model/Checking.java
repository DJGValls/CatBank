package CatBank.Model;

import CatBank.Model.Enums.Status;
import CatBank.Model.User.AccountHolder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "checking")
public class Checking extends AbstractAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int checkingId;

    @NotNull
    private Integer secretKey;

    @NotNull
    private BigDecimal minBalance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    //@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @NotNull
    private BigDecimal monthlyMaintenanceFee;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "AccountHolder")
    private AccountHolder accountHolder;

    public Checking() {
        super();
    }

    public Checking(String primaryOwner, String secundaryOwner, BigDecimal balance, Integer secretKey, Status status, AccountHolder accountHolder) {
        super(primaryOwner, secundaryOwner, balance);
        this.secretKey = secretKey;
        this.minBalance = BigDecimal.valueOf(250);
        this.status = status;
        this.creationDate = LocalDate.now();
        this.monthlyMaintenanceFee = BigDecimal.valueOf(12);
        this.accountHolder = accountHolder;
    }
    public int getCheckingId() {
        return checkingId;
    }

    public Integer getSecretKey() {
        return secretKey;
    }

    public BigDecimal getMinBalance() {
        return minBalance;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public BigDecimal getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public void setSecretKey(Integer secretKey) {
        this.secretKey = secretKey;
    }

    public void setMinBalance(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setMonthlyMaintenanceFee(BigDecimal monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public void setAccountHolder(AccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }

}
