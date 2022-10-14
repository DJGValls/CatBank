package CatBank.Utils;


import CatBank.Security.Model.Enums.RoleName;
import CatBank.Security.Model.Role;
import CatBank.Security.Repository.RoleRepository;
import CatBank.Security.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static CatBank.Security.Model.Enums.RoleName.*;

@Component
public class CreateRolesUtil implements CommandLineRunner {

    @Autowired
    RoleService roleService;
    @Autowired
    RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!roleRepository.existsByRoleName(ROLE_ADMIN)) {
            Role roleAdmin = new Role(ROLE_ADMIN);
            roleService.save(roleAdmin);
        }
        if (!roleRepository.existsByRoleName(ROLE_USERTHIRDPARTY)) {
            Role roleUser = new Role(ROLE_USERTHIRDPARTY);
            roleService.save(roleUser);
        }
        if (!roleRepository.existsByRoleName(ROLE_ACCOUNTHOLDER)) {
            Role roleUserAccountHolder = new Role(ROLE_ACCOUNTHOLDER);
            roleService.save(roleUserAccountHolder);
        }

    }
}









