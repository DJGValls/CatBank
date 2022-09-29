package CatBank.Model;

import CatBank.Model.Enums.Status;
import CatBank.Security.Model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

    @NotNull
    @ManyToMany
    @JoinTable(name = "user_student_checking", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "student_checking_id"))
    private Set<User> studentCheckingsHashset = new HashSet<>();

    public StudentChecking() {

    }

    public StudentChecking(String primaryOwner, String secundaryOwner, BigDecimal balance, BigDecimal penaltyFee, int studentCheckingId, String secretKey, Status status, Date creationDate) {
        super(primaryOwner, secundaryOwner, balance, penaltyFee);
        StudentCheckingId = studentCheckingId;
        this.secretKey = secretKey;
        this.status = status;
        this.creationDate = creationDate;
    }

    public int getStudentCheckingId() {
        return StudentCheckingId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Status getStatus() {
        return status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Set<User> getStudentCheckingsHashset() {
        return studentCheckingsHashset;
    }

    public void setStudentCheckingsHashset(Set<User> studentCheckingsHashset) {
        this.studentCheckingsHashset = studentCheckingsHashset;
    }
}
