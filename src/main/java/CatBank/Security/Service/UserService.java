package CatBank.Security.Service;

import CatBank.Security.Model.User;
import CatBank.Security.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

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
