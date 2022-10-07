package CatBank.CRON;

import CatBank.Model.Checking;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Service.CheckingService;
import CatBank.Utils.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
@Component
public class TaskAccountScheduler {

    @Autowired
    CheckingService checkingService;

    @Scheduled(cron = "*/10 * * * * *")
    public void scheduledTaskCheckingsFees(){

        long now = System.currentTimeMillis()/1000;
        System.out.println("schedule task using cron jobs" + now);
        //checkingService.feeApplycations()

    }

}
