package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.constants.RoleLevel;
import br.edu.ifmg.hotelbao.dtos.UserInsertDTO;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.repositories.*;
import br.edu.ifmg.hotelbao.services.exceptions.AccessDeniedException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private AuthService authService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Transactional
    public void deleteDatabase() {

        if (!authService.hasMinimumAuthority(RoleLevel.ROLE_ADMIN)) {
            throw new AccessDeniedException("[!] -> Only ADMINS can perform this operation!");
        }

        passwordRecoverRepository.deleteAll();
        stayRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
        addressRepository.deleteAll();

    }

}