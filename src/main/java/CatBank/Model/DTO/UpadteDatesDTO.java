package CatBank.Model.DTO;

import java.time.LocalDate;

public class UpadteDatesDTO {

    private LocalDate creationDate;

    private LocalDate lastMaintenanceAccount;

    public UpadteDatesDTO() {
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getLastMaintenanceAccount() {
        return lastMaintenanceAccount;
    }

    public void setLastMaintenanceAccount(LocalDate lastMaintenanceAccount) {
        this.lastMaintenanceAccount = lastMaintenanceAccount;
    }
}
