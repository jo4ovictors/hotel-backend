package br.edu.ifmg.hotelbao.util;

import br.edu.ifmg.hotelbao.dtos.RoomDTO;
import br.edu.ifmg.hotelbao.dtos.StayCreateDTO;
import br.edu.ifmg.hotelbao.dtos.UserInsertDTO;
import br.edu.ifmg.hotelbao.entities.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class Factory {

    public static Room createRoom() {
        Room entity = new Room();
        entity.setDescription("Room");
        entity.setPrice(new BigDecimal("199.99"));
        entity.setImageUrl("https://image.com/room.jpg");
        return entity;
    }

    public static RoomDTO createRoomDTO() {
        Room entity = createRoom();
        return new RoomDTO(entity);
    }

    public static Stay createStay() {
        Stay entity = new Stay();
        entity.setRoom(createRoom());
        entity.setUser(createUser());
        entity.setPrice(entity.getRoom().getPrice());
        entity.setCheckIn(LocalDate.now());
        entity.setCheckOut(LocalDate.now().plusDays(1));
        return entity;
    }

    public static Address createAddress(){
        Address entity = new Address();
        entity.setId(1L);
        entity.setStreet("Rua das Flores, 123");
        entity.setCity("Belo Horizonte");
        entity.setCountry("Brazil");
        entity.setState("MG");

        return entity;
    }

    public static StayCreateDTO createStayDTO() {
        Stay entity = createStay();
        return new StayCreateDTO(entity);
    }

    public static User createUser() {
        User entity = new User();
        entity.setFirstName("First Name");
        entity.setLastName("Last Name");
        entity.setEmail("newuser@email.com");
        entity.setLogin("login");
        entity.setPhone("99911223344");
        entity.setPassword("111111111");

        entity.setRoles(Set.of(new Role(3L, "ROLE_CLIENT")));
        entity.setAddress(createAddress());

        return entity;
    }

    public static UserInsertDTO createUserDTO() {
        User entity = createUser();
        return new UserInsertDTO(entity);
    }

}
