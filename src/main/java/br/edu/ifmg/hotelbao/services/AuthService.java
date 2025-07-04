package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.repository.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("[!] -> Unauthenticated User!");
        }

        String login = jwt.getClaimAsString("username");

        if (login == null || login.isBlank()) {
            throw new AccessDeniedException("[!] -> JWT token does not contain the 'username' claim!");
        }

        return userRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFound("[!] -> User not found: " + login + "!"));
    }

    public boolean doesNotHaveAuthority(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("[!] -> Unauthenticated User!");
        }

        var roles = jwt.getClaimAsStringList("authorities");

        return (roles == null || !roles.contains(role));
    }

    public boolean isClient(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> "ROLE_CLIENT".equals(role.getAuthority()));
    }

    public boolean isAdminOrEmployee(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> "ROLE_ADMIN".equals(role.getAuthority()) || "ROLE_EMPLOYEE".equals(role.getAuthority()));
    }

}
