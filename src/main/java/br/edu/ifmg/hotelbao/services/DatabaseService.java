package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

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

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public void deleteDatabase() {
        addressRepository.deleteAll();
        passwordRecoverRepository.deleteAll();
        userRepository.deleteAll();
        roomRepository.deleteAll();
        stayRepository.deleteAll();
    }

}
