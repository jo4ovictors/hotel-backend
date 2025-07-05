package br.edu.ifmg.hotelbao.util;

import br.edu.ifmg.hotelbao.dtos.RoomDTO;
import br.edu.ifmg.hotelbao.dtos.StayCreateDTO;
import br.edu.ifmg.hotelbao.dtos.StayUpdateDTO;
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
        entity.setIsActive(true);
        return entity;
    }

    public static RoomDTO createRoomDTO() {
        Room entity = createRoom();
        return new RoomDTO(entity);
    }

    public static StayCreateDTO createStayDTO(Long userId, Long roomId) {
        StayCreateDTO dto = new StayCreateDTO();
        dto.setUserId(userId);
        dto.setRoomId(roomId);
        dto.setCheckIn(LocalDate.now().plusDays(1));
        dto.setCheckOut(LocalDate.now().plusDays(3));
        return dto;
    }

    public static StayUpdateDTO createUpcomingStayUpdateDTO(Long userId, Long roomId) {
        StayUpdateDTO dto = new StayUpdateDTO();
        dto.setUserId(userId);
        dto.setRoomId(roomId);
        dto.setCheckIn(LocalDate.now().plusDays(1));
        dto.setCheckOut(LocalDate.now().plusDays(3));
        return dto;
    }

    public static User createUser() {
        User entity = new User();
        entity.setFirstName("FirstName");
        entity.setLastName("LastName");
        entity.setEmail("newuser@email.com");
        entity.setLogin("login");
        entity.setPhone("99911223344");
        entity.setPassword("12345678");

        entity.setRoles(Set.of(new Role(3L, "ROLE_CLIENT")));
        entity.setAddress(createAddress());

        return entity;
    }

    public static UserInsertDTO createUserDTO() {
        User entity = createUser();
        return new UserInsertDTO(entity);
    }

    public static Address createAddress(){
        Address entity = new Address();
        entity.setStreet("Rua das Flores, 123");
        entity.setCity("Belo Horizonte");
        entity.setCountry("Brazil");
        entity.setPostalCode("12345-6789");
        entity.setState("MG");
        return entity;
    }

}
