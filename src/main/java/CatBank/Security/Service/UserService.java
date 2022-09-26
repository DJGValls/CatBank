package CatBank.Security.Service;

import CatBank.Security.Model.User;
import CatBank.Security.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    UserRepository userRepository;

    public Optional<User> getByUserName(String userName){
        return userRepository.findByUserName(userName);
    }

    public Boolean existsByUserName(String userName){
        return userRepository.existsByUserName(userName);
    }

    public void save(User user){
        userRepository.save(user);
    }

}
