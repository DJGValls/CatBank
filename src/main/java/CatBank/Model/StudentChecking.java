package CatBank.Model;

import CatBank.Model.Enums.Status;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "studentChecking")
public class StudentChecking extends AbstractAccount{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int StudentCheckingId;

    @NotNull
    private String secretKey;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    private Date creationDate;


    public StudentChecking() {

    }


}
