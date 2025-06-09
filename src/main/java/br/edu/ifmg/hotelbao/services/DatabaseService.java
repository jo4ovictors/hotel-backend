package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.repository.RoleRepository;
import br.edu.ifmg.hotelbao.repository.RoomRepository;
import br.edu.ifmg.hotelbao.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public void deleteDatabase() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        roomRepository.deleteAll();
    }

}
