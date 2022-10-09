package CatBank.Security.Service;

import CatBank.Security.DTO.JwtDTO;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.DTO.NewUserDTO;
import CatBank.Security.DTO.UserLoginDTO;
import CatBank.Security.JasonWebToken.JwtProvider;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.User;
import CatBank.Security.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;





    public Optional<User> getByUserName(String userName){
        return userRepository.findByUserName(userName);
    }

    public List<User> listAdmins(){
        return userRepository.findByUserNameContains("admin");
    }

    public List<User> listUsers(){
        return userRepository.findByUserNameNotContains("admin");
    }

    public Boolean existsByUserName(String userName){
        return userRepository.existsByUserName(userName);
    }
    public void save(User user){
        userRepository.save(user);
    }

    public boolean existsByUserId(int userId){
        return userRepository.existsById(userId);
    }
    public void deleteUser(int userId){
        userRepository.deleteById(userId);
    }




}
