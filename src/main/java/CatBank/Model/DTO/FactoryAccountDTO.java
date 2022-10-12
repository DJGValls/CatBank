package CatBank.Model.DTO;

import CatBank.Model.Enums.Status;
import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.ThirdParty;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

public class FactoryAccountDTO {

    @NotNull
    private String primaryOwner;
    @Nullable
    private String secundaryOwner;

    private MoneyDTO balance;
    @NotNull
    private Integer SecretKey;
    @NotNull
    private Status status;
    @NotNull
    private AccountHolder accountHolder;

    public FactoryAccountDTO() {
    }

    public String getPrimaryOwner() {
        return primaryOwner;
    }

    public String getSecundaryOwner() {
        return secundaryOwner;
    }

    public MoneyDTO getBalance() {
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

    public void setBalance(MoneyDTO balance) {
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
