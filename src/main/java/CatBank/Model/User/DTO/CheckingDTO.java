package CatBank.Model.User.DTO;

import CatBank.Model.Enums.Status;
import CatBank.Model.User.AccountHolder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CheckingDTO {


    @NotNull
    private String primaryOwner;
    @NotNull
    private String secundaryOwner;

    private BigDecimal balance;
    @NotNull
    private Integer SecretKey;
    @NotNull
    private Status status;
    @NotNull
    private AccountHolder accountHolder;


    public CheckingDTO() {
    }

    public String getPrimaryOwner() {
        return primaryOwner;
    }

    public String getSecundaryOwner() {
        return secundaryOwner;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Integer getSecretKey() {
        return SecretKey;
    }

    public Status getStatus() {
        return status;
    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public void setPrimaryOwner(String primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public void setSecundaryOwner(String secundaryOwner) {
        this.secundaryOwner = secundaryOwner;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setSecretKey(Integer secretKey) {
        SecretKey = secretKey;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setAccountHolder(AccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }
}