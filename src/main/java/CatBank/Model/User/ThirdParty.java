package CatBank.Model.User;

import CatBank.Model.Checking;
import CatBank.Model.StudentChecking;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "thirdParty")
public class ThirdParty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int thirdPartyId;
    @NotBlank
    private String userName;
    @NotBlank
    private String password;
    @OneToMany(mappedBy = "checkingId")
    @JsonIgnore
    private List<Checking> checkingList;
    @OneToMany(mappedBy = "studentCheckingId")
    @JsonIgnore
    private List<StudentChecking> studentCheckingsList;

    public ThirdParty(String userName, String password, List<Checking> checkingList, List<StudentChecking> studentCheckingsList) {
        this.userName = userName;
        this.password = password;
        this.checkingList = checkingList;
        this.studentCheckingsList = studentCheckingsList;
    }

    public ThirdParty() {
    }

    public int getThirdPartyId() {
        return thirdPartyId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public List<Checking> getCheckingList() {
        return checkingList;
    }

    public List<StudentChecking> getStudentCheckingsList() {
        return studentCheckingsList;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCheckingList(List<Checking> checkingList) {
        this.checkingList = checkingList;
    }

    public void setStudentCheckingsList(List<StudentChecking> studentCheckingsList) {
        this.studentCheckingsList = studentCheckingsList;
    }
}
