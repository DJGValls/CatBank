package CatBank.Model;

import CatBank.Model.Enums.Status;
import CatBank.Model.User.AccountHolder;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "checking")
public class Checking extends AbstractAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int checkingId;

    @NotNull
    private String secretKey;

    @NotNull
    private BigDecimal minBalance;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @DateTimeFormat(pattern = "dd-mm-yyyy")
    private Date creationDate;

    @NotNull
    private BigDecimal monthlyMaintenanceFee;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "accountHolder_user")
    private AccountHolder accountHolderUser;

    public Checking() {
        super();
    }

    public Checking(String primaryOwner, String secundaryOwner, BigDecimal balance, BigDecimal penaltyFee, String secretKey, BigDecimal minBalance, Status status, Date creationDate, BigDecimal monthlyMaintenanceFee, AccountHolder accountHolderUser) {
        super(primaryOwner, secundaryOwner, balance, penaltyFee);
        this.checkingId = checkingId;
        this.secretKey = secretKey;
        this.minBalance = minBalance;
        this.status = status;
        this.creationDate = creationDate;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
        this.accountHolderUser = accountHolderUser;
    }

    public int getCheckingId() {
        return checkingId;
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

    public BigDecimal getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public AccountHolder getAccountHolderUser() {
        return accountHolderUser;
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

    public void setMonthlyMaintenanceFee(BigDecimal monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public void setAccountHolderUser(AccountHolder checkingUser) {
        this.accountHolderUser = checkingUser;
    }



}
