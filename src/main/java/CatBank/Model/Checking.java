package CatBank.Model;

import CatBank.Model.Enums.Status;
import CatBank.Model.User.AccountHolder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "checking")
public class Checking extends AbstractAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int checkingId;

    @NotNull
    private Integer secretKey;

    @NotNull
    private BigDecimal minBalance = BigDecimal.valueOf(250);

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false)
    private Calendar creationDate = Calendar.getInstance();

    @NotNull
    private BigDecimal monthlyMaintenanceFee = BigDecimal.valueOf(12);

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "AccountHolder")
    private AccountHolder accountHolder;

    public Checking() {
        super();
    }

    public Checking(String primaryOwner, String secundaryOwner, BigDecimal balance, BigDecimal penaltyFee, Integer secretKey, BigDecimal minBalance, Status status, Calendar creationDate, BigDecimal monthlyMaintenanceFee, AccountHolder accountHolder) {
        super(primaryOwner, secundaryOwner, balance, penaltyFee);
        this.secretKey = secretKey;
        this.minBalance = minBalance;
        this.status = status;
        this.creationDate = creationDate;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
        this.accountHolder = accountHolder;
    }

    public Checking(String primaryOwner, String secundaryOwner, BigDecimal balance, Integer secretKey, Status status, AccountHolder accountHolder) {
        super(primaryOwner, secundaryOwner, balance);
        this.secretKey = secretKey;
        this.status = status;
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

    public Calendar getCreationDate() {
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

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public void setMonthlyMaintenanceFee(BigDecimal monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public void setAccountHolder(AccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }

}
