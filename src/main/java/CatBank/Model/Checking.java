package CatBank.Model;

import CatBank.Model.Enums.Status;
import CatBank.Security.Model.User;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @NotNull
    @ManyToMany
    @JoinTable(name = "user_checking", joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "checking_id"))
    private Set<User> checkingsHashset = new HashSet<>();


    public Checking(String primaryOwner, String secundaryOwner, BigDecimal balance, BigDecimal penaltyFee, int checkingId, String secretKey, BigDecimal minBalance, Status status, Date creationDate, BigDecimal monthlyMaintenanceFee) {
        super(primaryOwner, secundaryOwner, balance, penaltyFee);
        this.checkingId = checkingId;
        this.secretKey = secretKey;
        this.minBalance = minBalance;
        this.status = status;
        this.creationDate = creationDate;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public Checking() {
        super();
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

    public Set<User> getCheckingsHashset() {
        return checkingsHashset;
    }

    public void setCheckingsHashset(Set<User> checkingsHashset) {
        this.checkingsHashset = checkingsHashset;
    }
}
