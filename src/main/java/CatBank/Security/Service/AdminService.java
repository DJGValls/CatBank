package CatBank.Security.Service;

import CatBank.Model.User.AccountHolder;
import CatBank.Security.DTO.JwtDTO;
import CatBank.Security.DTO.NewUserDTO;
import CatBank.Security.DTO.UserLoginDTO;
import CatBank.Security.Model.User;
import org.springframework.http.ResponseEntity;

public interface AdminService {

    ResponseEntity<?> createAdminUser(NewUserDTO newUserDTO);

    ResponseEntity<JwtDTO> loginGenerator(UserLoginDTO userLoginDTO);

}
