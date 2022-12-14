package CatBank.Service;

import CatBank.Model.User.ThirdParty;
import CatBank.Repository.ThirdPartyRepository;
import CatBank.Security.DTO.MensajeDTO;
import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.Role;
import CatBank.Security.Model.User;
import CatBank.Security.Repository.UserRepository;
import CatBank.Security.Service.RoleService;
import CatBank.Security.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ThirdPartyServiceImp implements ThirdPartyService{
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    RoleService roleService;
    @Autowired
    ThirdPartyRepository thirdPartyRepository;

    @Override
    public ThirdParty save(ThirdParty thirdParty) {
        return thirdPartyRepository.save(thirdParty);
    }

    @Override
    public void delete(int thirdPartyId) {
        thirdPartyRepository.deleteById(thirdPartyId);
    }

    @Override
    public List<ThirdParty> thirdPartysList() {
        return thirdPartyRepository.findAll();
    }

    @Override
    public Optional<ThirdParty> findByUserName(String userName) {
        return thirdPartyRepository.findByUserName(userName);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return thirdPartyRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByThirdPartyId(Integer thirdPartyId){
        return thirdPartyRepository.existsByThirdPartyId(thirdPartyId);
    }
    @Override
    public ThirdParty thirdPartyFactory(ThirdParty thirdParty) {
        ThirdParty thirdParty1 = new ThirdParty(thirdParty.getUserName(), passwordEncoder.encode(thirdParty.getPassword()));
        return save(thirdParty1);
    }

    @Override
    public ThirdParty findByThirdPartyUserName(String userName) {
        return thirdPartyRepository.findByUserName(userName).get();
    }

    @Override
    public Optional createThirdParty(ThirdParty thirdParty) {
        if (userService.existsByUserName(thirdParty.getUserName())) {
            return null;
        }
        User user = new User(thirdParty.getUserName(), passwordEncoder.encode(thirdParty.getPassword()));
        Set<Role> roles = new HashSet<>();
        roles.add((roleService.getByRoleName(RoleName.ROLE_USERTHIRDPARTY).get()));
        if (thirdParty.getUserName().contains("admin")) {
            return null;
        }
        user.setRoles(roles);
        userService.save(user);
        thirdPartyFactory(thirdParty);
        return userRepository.findById(user.getUserId());
    }
}
