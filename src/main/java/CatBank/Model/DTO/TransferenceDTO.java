package CatBank.Model.DTO;

import CatBank.Model.Enums.AccountType;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferenceDTO {

    @NotNull
    private Integer originId;
    @NotNull
    private String originName;
    @NotNull
    private Integer secretKey;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer destinyId;
    @NotNull
    private String destinyName;
    @NotNull
    private AccountType destinyAccountType;


    public TransferenceDTO() {
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getDestinyName() {
        return destinyName;
    }

    public void setDestinyName(String destinyName) {
        this.destinyName = destinyName;
    }

    public Integer getOriginId() {
        return originId;
    }

    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getDestinyId() {
        return destinyId;
    }

    public void setDestinyId(Integer destinyId) {
        this.destinyId = destinyId;
    }

    public AccountType getDestinyAccountType() {
        return destinyAccountType;
    }

    public void setDestinyAccountType(AccountType destinyAccountType) {
        this.destinyAccountType = destinyAccountType;
    }

    @Nullable
    public Integer getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(@Nullable Integer secretKey) {
        this.secretKey = secretKey;
    }
}
