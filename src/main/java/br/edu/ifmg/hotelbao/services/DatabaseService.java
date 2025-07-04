package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.repository.*;
import jakarta.transaction.Transactional;
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
    @Transactional
    public void deleteDatabase(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário com ID " + id + " não encontrado"));

        stayRepository.deleteAll();
        passwordRecoverRepository.deleteAll();

        roomRepository.deleteAll();
        userRepository.deleteAllExcept(id);
        addressRepository.deleteAllExcept(user.getAddress().getId());
    }


}
