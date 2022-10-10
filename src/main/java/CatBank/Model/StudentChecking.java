package CatBank.Model;

import CatBank.Model.Enums.AccountType;
import CatBank.Model.Enums.Status;
import CatBank.Model.User.AccountHolder;
import CatBank.Utils.Money;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "studentChecking")
@EntityListeners(AuditingEntityListener.class)
public class StudentChecking extends AbstractAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int StudentCheckingId;

    @NotNull
    private Integer secretKey;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "AccountHolder")
    private AccountHolder accountHolder;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AccountType accountType;


    public StudentChecking() {

    }

    public StudentChecking(String primaryOwner, String secundaryOwner, Money balance, Integer secretKey, Status status, AccountHolder accountHolder) {
        super(primaryOwner, secundaryOwner, balance);
        this.secretKey = secretKey;
        this.status = status;
        this.creationDate = LocalDate.now();
        this.accountHolder = accountHolder;
        this.accountType = AccountType.STUDENTCHECKING;
    }

    public int getStudentCheckingId() {
        return StudentCheckingId;
    }

    public Integer getSecretKey() {
        return secretKey;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public void setSecretKey(Integer secretKey) {
        this.secretKey = secretKey;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public void setAccountHolder(AccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }
}
