package CatBank.Model.DTO;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferenceDTO {

    @NotNull
    private Integer originId;
    @NotNull
    private String originName;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer destinyId;
    @NotNull
    private String destinyName;


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
}
