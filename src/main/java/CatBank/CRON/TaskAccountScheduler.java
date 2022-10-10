package CatBank.CRON;

import CatBank.Model.Checking;
import CatBank.Model.Savings;
import CatBank.Repository.CheckingRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Service.CheckingService;
import CatBank.Service.SavingsService;
import CatBank.Utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class TaskAccountScheduler {

    @Autowired
    CheckingService checkingService;

    @Autowired
    CheckingRepository checkingRepository;

    @Autowired
    SavingsService savingsService;

    @Scheduled(cron = "*/10 * * * * *")
    public void scheduledTaskCheckingsFees(){

        List<Checking> checkingsList = new ArrayList<>(checkingService.checkingsList());
        for(Checking checking1 : checkingsList) {
            while(LocalDate.now().isAfter(checking1.getLastMaintenanceFee().plusMonths(1))){
                checking1.setLastMaintenanceFee(checking1.getLastMaintenanceFee().plusMonths(1));
                checking1.setBalance(new Money(checking1.getBalance().decreaseAmount(checking1.getMonthlyMaintenanceFee().getAmount())));
                checkingService.penaltyFeeApply(checking1.getCheckingId());
                checkingRepository.save(checking1);
            }
        }
    }
    @Scheduled(cron = "*/60 * * * * *")
    public void scheduledTaskSavingsFeesAndInterestRate(){

        List<Savings> savingsList = new ArrayList<>(savingsService.savingsList());
        for(Savings storedSavings : savingsList) {
            while(LocalDate.now().isAfter(storedSavings.getLastMaintenanceAccount().plusMonths(12))){
                storedSavings.setLastMaintenanceAccount(storedSavings.getLastMaintenanceAccount().plusMonths(12));
                storedSavings.setBalance(new Money(storedSavings.getBalance().increaseAmount(((storedSavings.getInterestRate().getAmount()).multiply(storedSavings.getBalance().getAmount()).divide(BigDecimal.valueOf(100))))));

                if (storedSavings.getBalance().getAmount().compareTo(storedSavings.getMinBalance().getAmount())==-1) {
                    storedSavings.getBalance().decreaseAmount(storedSavings.getPenaltyFee().getAmount());
                }savingsService.save(storedSavings);
            }savingsService.save(storedSavings);

        }
    }

}
