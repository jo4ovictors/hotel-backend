package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.dtos.*;
import br.edu.ifmg.hotelbao.entities.Address;
import br.edu.ifmg.hotelbao.entities.Role;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.projections.UserDetailsProjection;
import br.edu.ifmg.hotelbao.repository.AddressRepository;
import br.edu.ifmg.hotelbao.repository.RoleRepository;
import br.edu.ifmg.hotelbao.repository.StayRepository;
import br.edu.ifmg.hotelbao.repository.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFound;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFound("[!] -> User not found!"));
        return new UserDTO(user);
    }

    private Address convertAddressDTOToEntity(AddressDTO dto, Address existingAddress) {
        Address address = existingAddress != null ? existingAddress : new Address();

        if (dto.getStreet() != null) address.setStreet(dto.getStreet());
        if (dto.getCity() != null) address.setCity(dto.getCity());
        if (dto.getState() != null) address.setState(dto.getState());
        if (dto.getPostalCode() != null) address.setPostalCode(dto.getPostalCode());
        if (dto.getCountry() != null) address.setCountry(dto.getCountry());

        return address;
    }

    private void  copyDTOToEntity(UserDTO dto, User entity) {

        if (dto.getFirstName() != null) entity.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) entity.setLastName(dto.getLastName());
        if (dto.getEmail() != null) entity.setEmail(dto.getEmail());
        if (dto.getLogin() != null) entity.setLogin(dto.getLogin());
        if (dto.getPhone() != null) entity.setPhone(dto.getPhone());

        if (dto.getAddress() != null) {
            Address updatedAddress = convertAddressDTOToEntity(dto.getAddress(), entity.getAddress());
            entity.setAddress(updatedAddress);
        }

        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            entity.getRoles().clear();
            for (RoleDTO role : dto.getRoles()) {
                Role r = roleRepository.findById(role.getId())
                        .orElseThrow(() -> new ResourceNotFound("Role not found: " + role.getId()));

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
    public UserDetails loadUserByUsername(String username) throws ResourceNotFound {
        List<UserDetailsProjection> result = userRepository.searchUserAndRoleByLogin(username);

        if (result.isEmpty()) {
            throw new ResourceNotFound("[!] -> User not found: " + username + "!");
        }

        return buildUserWithRoles(result);
    }

    public UserDTO signup(@Valid UserInsertDTO dto) {
        User entity = new User();
        copyDTOToEntity(dto, entity);

//        Role role = roleRepository.findByAuthority("ROLE_CLIENT");
//        entity.getRoles().clear();
//        entity.getRoles().add(role);

        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        User novo = userRepository.save(entity);
        return new UserDTO(novo);
    }

    public InvoiceDTO newInvoice(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFound("[!] -> User not found!"));

        UserInvoiceDTO uerInvoiceDTO = new UserInvoiceDTO(user);

        List<StayItemDTO> stays = stayRepository.findStayItemsByUserId(userId);

        if (stays.isEmpty()) {
            throw new ResourceNotFound("[!] -> The user does not have any stays for the invoice!");
        }

        BigDecimal total = stays.stream()
                .filter(e -> e.getDescription() != null && e.getPrice() != null)
                .map(StayItemDTO::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InvoiceDTO(uerInvoiceDTO, stays, total);
    }

    public User findByLogin(String username) {
        return userRepository.findByLogin(username).orElseThrow(() -> new ResourceNotFound("User not found"));
    }
}
