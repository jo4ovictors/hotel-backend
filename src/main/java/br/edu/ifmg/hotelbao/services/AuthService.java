package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.constants.RoleLevel;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.repositories.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;


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
                .orElseThrow(() -> new ResourceNotFoundException("[!] -> User not found: " + login + "!"));
    }

    public Optional<RoleLevel> getHighestRoleLevel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AccessDeniedException("[!] -> Unauthenticated User!");
        }

        var roles = jwt.getClaimAsStringList("authorities");

        if (roles == null || roles.isEmpty()) return Optional.empty();

        return roles.stream()
                .map(RoleLevel::fromString)
                .flatMap(Optional::stream)
                .max(Comparator.comparingInt(RoleLevel::getLevel));
    }

    public boolean hasMinimumAuthority(RoleLevel requiredLevel) {
        return getHighestRoleLevel()
                .map(current -> current.getLevel() >= requiredLevel.getLevel())
                .orElse(false);
    }

}
