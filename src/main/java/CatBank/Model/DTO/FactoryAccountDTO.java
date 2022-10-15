package CatBank.Model.DTO;

import CatBank.Model.Enums.Status;
import CatBank.Model.User.AccountHolder;
import CatBank.Model.User.ThirdParty;
import CatBank.Utils.Money;
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

    public FactoryAccountDTO(String primaryOwner, @Nullable String secundaryOwner, MoneyDTO balance, Integer secretKey, Status status, AccountHolder accountHolder) {
        this.primaryOwner = primaryOwner;
        this.secundaryOwner = secundaryOwner;
        this.balance = balance;
        SecretKey = secretKey;
        this.status = status;
        this.accountHolder = accountHolder;
    }

    public FactoryAccountDTO(MoneyDTO balance) {
        this.balance = balance;
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
