package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.dtos.RoleDTO;
import br.edu.ifmg.hotelbao.dtos.UserDTO;
import br.edu.ifmg.hotelbao.dtos.UserInsertDTO;
import br.edu.ifmg.hotelbao.entities.Role;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.projections.UserDetailsProjection;
import br.edu.ifmg.hotelbao.repository.RoleRepository;
import br.edu.ifmg.hotelbao.repository.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFound("[!] -> User not found!"));
        return new UserDTO(user);
    }

    private void copyDTOToEntity(UserDTO dto, User entity) {

        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getLogin() != null) entity.setLogin(dto.getLogin());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());

        if (!dto.getRoles().isEmpty()) {
            entity.getRoles().clear();
            for (RoleDTO role : dto.getRoles()) {
                Role r = roleRepository.getReferenceById(role.getId());
                entity.getRoles().add(r);
            }
        }
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        User entity = new User();
        copyDTOToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        User new_user = userRepository.save(entity);
        return new UserDTO(new_user);
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User entity = userRepository.getReferenceById(id);
            copyDTOToEntity(dto, entity);
            entity = userRepository.save(entity);
            return new UserDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFound("[!] -> User not found: " + id + "!");
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
           throw new ResourceNotFound("[!] -> User not found: " + id + "!");
        }

        try {
            userRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new ResourceNotFound("[!] -> Integrity Violation!");
        }
    }

    private User buildUserWithRoles(List<UserDetailsProjection> result) {
        User user = new User();
        UserDetailsProjection first = result.getFirst();
        user.setLogin(first.getUsername());
        user.setPassword(first.getPassword());

        result.forEach(p -> user.addRole(new Role(p.getRoleId(), p.getAuthority())));
        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRoleByLogin(username);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("[!] -> User not found: " + username + "!");
        }

        return buildUserWithRoles(result);
    }

}
