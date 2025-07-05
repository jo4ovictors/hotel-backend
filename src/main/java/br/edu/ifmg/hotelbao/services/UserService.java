package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.constants.RoleLevel;
import br.edu.ifmg.hotelbao.dtos.*;
import br.edu.ifmg.hotelbao.entities.Address;
import br.edu.ifmg.hotelbao.entities.Role;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.projections.UserDetailsProjection;
import br.edu.ifmg.hotelbao.repositories.AddressRepository;
import br.edu.ifmg.hotelbao.repositories.RoleRepository;
import br.edu.ifmg.hotelbao.repositories.StayRepository;
import br.edu.ifmg.hotelbao.repositories.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.AccessDeniedException;
import br.edu.ifmg.hotelbao.services.exceptions.DatabaseException;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFoundException;
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
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired private UserRepository userRepository;
    @Autowired private AddressRepository addressRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private StayRepository stayRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private AuthService authService;

    private static final String NOT_FOUND_MSG = "[!] -> User not found!";

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        if (authService.hasMinimumAuthority(RoleLevel.ROLE_ADMIN)) {
            return userRepository.findAll(pageable).map(UserDTO::new);
        }
        return userRepository.findByRole("ROLE_CLIENT", pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User currentUser = authService.getAuthenticatedUser();

        if (authService.hasMinimumAuthority(RoleLevel.ROLE_ADMIN)) {
            return new UserDTO(getUserById(id));
        }

        if (authService.hasMinimumAuthority(RoleLevel.ROLE_EMPLOYEE)) {
            if (currentUser.getId().equals(id)) return new UserDTO(currentUser);

            User target = getUserById(id);
            if (isClient(target)) return new UserDTO(target);

            throw new AccessDeniedException("[!] -> EMPLOYEE can only access CLIENT users or their own data!");
        }

        if (currentUser.getId().equals(id)) return new UserDTO(currentUser);
        throw new AccessDeniedException("[!] -> CLIENT can only access their own data!");
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto) {
        if (isEmployeeButNotAdmin() && !onlyContainsClientRole(dto)) {
            throw new AccessDeniedException("[!] -> EMPLOYEE can only create CLIENT users!");
        }

        User entity = new User();
        copyDTOToEntity(dto, entity);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        return new UserDTO(userRepository.save(entity));
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        User target = getUserById(id);

        if (isEmployeeButNotAdmin() && !isClient(target)) {
            throw new AccessDeniedException("[!] -> EMPLOYEE can only update CLIENT users!");
        }

        copyDTOToEntity(dto, target);
        return new UserDTO(userRepository.save(target));
    }

    @Transactional
    public void delete(Long id) {
        User target = getUserById(id);

        if (isEmployeeButNotAdmin() && !isClient(target)) {
            throw new AccessDeniedException("[!] -> EMPLOYEE can only delete CLIENT users!");
        }

        try {
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("[!] -> Integrity violation!");
        }
    }

    @Transactional
    public UserDTO signup(@Valid UserInsertDTO dto) {
        User entity = new User();
        copyDTOToEntity(dto, entity);

        Role clientRole = roleRepository.findByAuthority("ROLE_CLIENT");
        entity.getRoles().clear();
        entity.getRoles().add(clientRole);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));

        return new UserDTO(userRepository.save(entity));
    }

    @Transactional
    public InvoiceDTO newInvoice(Long userId) {
        User currentUser = authService.getAuthenticatedUser();

        if (authService.hasMinimumAuthority(RoleLevel.ROLE_ADMIN)) {
            return buildInvoice(userId);
        }

        if (authService.hasMinimumAuthority(RoleLevel.ROLE_EMPLOYEE)) {
            if (currentUser.getId().equals(userId)) return buildInvoice(userId);

            User target = getUserById(userId);
            if (isClient(target)) return buildInvoice(userId);

            throw new AccessDeniedException("[!] -> EMPLOYEE can only generate invoices for CLIENTS or themselves.");
        }

        if (currentUser.getId().equals(userId)) return buildInvoice(userId);
        throw new AccessDeniedException("[!] -> You do not have permission to generate this invoice!");
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        List<UserDetailsProjection> result = userRepository.searchUserAndRoleByLogin(username);
        if (result.isEmpty()) throw new ResourceNotFoundException(NOT_FOUND_MSG + " " + username + "!");
        return buildUserWithRoles(result);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_MSG));
    }

    private boolean isClient(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> "ROLE_CLIENT".equals(role.getAuthority()));
    }

    private boolean onlyContainsClientRole(UserInsertDTO dto) {
        return dto.getRoles().stream()
                .map(RoleDTO::getAuthority)
                .allMatch("ROLE_CLIENT"::equals);
    }

    private boolean isEmployeeButNotAdmin() {
        return authService.hasMinimumAuthority(RoleLevel.ROLE_EMPLOYEE) &&
                !authService.hasMinimumAuthority(RoleLevel.ROLE_ADMIN);
    }

    private void copyDTOToEntity(UserDTO dto, User entity) {
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
            dto.getRoles().forEach(roleDTO -> {
                Role role = roleRepository.getReferenceById(roleDTO.getId());
                entity.getRoles().add(role);
            });
        }
    }

    private Address convertAddressDTOToEntity(AddressDTO dto, Address existing) {
        Address address = Optional.ofNullable(existing).orElse(new Address());
        if (dto.getStreet() != null) address.setStreet(dto.getStreet());
        if (dto.getCity() != null) address.setCity(dto.getCity());
        if (dto.getState() != null) address.setState(dto.getState());
        if (dto.getPostalCode() != null) address.setPostalCode(dto.getPostalCode());
        if (dto.getCountry() != null) address.setCountry(dto.getCountry());
        return address;
    }

    private User buildUserWithRoles(List<UserDetailsProjection> result) {
        User user = new User();
        UserDetailsProjection first = result.getFirst();
        user.setLogin(first.getUsername());
        user.setPassword(first.getPassword());
        result.forEach(p -> user.addRole(new Role(p.getRoleId(), p.getAuthority())));
        return user;
    }

    private InvoiceDTO buildInvoice(Long userId) {
        User user = getUserById(userId);
        UserInvoiceDTO invoiceUser = new UserInvoiceDTO(user);
        List<StayItemDTO> stays = stayRepository.findStayItemsByUserId(userId);

        if (stays.isEmpty()) {
            throw new ResourceNotFoundException("[!] -> The user has no stays for the invoice!");
        }

        BigDecimal total = stays.stream()
                .map(StayItemDTO::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InvoiceDTO(invoiceUser, stays, total);
    }

}
