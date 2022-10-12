package CatBank.Model.DTO;

import CatBank.Model.User.ThirdParty;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

public class ThirdPartyFactoryAccountDTO {
    @NotNull
    private String primaryOwner;
    @Nullable
    private String secundaryOwner;
    private MoneyDTO balance;
    @NotNull
    private Integer SecretKey;
    @NotNull
    private ThirdParty thirdParty;

    public ThirdPartyFactoryAccountDTO() {
    }

    public String getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(String primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public MoneyDTO getBalance() {
        return balance;
    }

    public void setBalance(MoneyDTO balance) {
        this.balance = balance;
    }

    public Integer getSecretKey() {
        return SecretKey;
    }

    public void setSecretKey(Integer secretKey) {
        SecretKey = secretKey;
    }

    public ThirdParty getThirdParty() {
        return thirdParty;
    }

    public void setThirdParty(ThirdParty thirdParty) {
        this.thirdParty = thirdParty;
    }

    @Nullable
    public String getSecundaryOwner() {
        return secundaryOwner;
    }

    public void setSecundaryOwner(@Nullable String secundaryOwner) {
        this.secundaryOwner = secundaryOwner;
    }
}
