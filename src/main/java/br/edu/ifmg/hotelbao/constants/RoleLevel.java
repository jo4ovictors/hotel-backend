package br.edu.ifmg.hotelbao.constants;

import java.util.Arrays;
import java.util.Optional;


public enum RoleLevel {

    ROLE_CLIENT(1),
    ROLE_EMPLOYEE(2),
    ROLE_ADMIN(3);

    private final int level;

    RoleLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

    public static Optional<RoleLevel> fromString(String role) {
        return Arrays.stream(values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst();
    }

}
