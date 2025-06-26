package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.Stay;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Data Transfer Object used for returning detailed stay information")
public class StayResponseDTO extends RepresentationModel<StayResponseDTO> {

    @Schema(description = "Unique identifier of the stay", example = "1001")
    private Long id;

    @Schema(description = "ID of the user who made the reservation", example = "501")
    private Long userId;

    @Schema(description = "Name of the user", example = "John Doe")
    private String userName;

    @Schema(description = "ID of the reserved room", example = "302")
    private Long roomId;

    @Schema(description = "Description of the room", example = "Deluxe Suite with Sea View")
    private String roomDescription;

    @Schema(description = "Check-in date of the stay", example = "2025-07-01")
    private LocalDate checkIn;

    @Schema(description = "Check-out date of the stay", example = "2025-07-05")
    private LocalDate checkOut;

    @Schema(description = "Total price of the stay", example = "1250.00", type = "string", format = "decimal")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    public StayResponseDTO() {
    }

    public StayResponseDTO(Long id, Long userId, String userName, Long roomId, String roomDescription, LocalDate checkIn, LocalDate checkOut, BigDecimal price) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.roomId = roomId;
        this.roomDescription = roomDescription;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.price = price;
    }

    public StayResponseDTO(Stay entity) {
        this.id = entity.getId();
        this.userId = entity.getUser().getId();
        this.userName = entity.getUser().getFirstName();
        this.roomId = entity.getRoom().getId();
        this.roomDescription = entity.getRoom().getDescription();
        this.checkIn = entity.getCheckIn();
        this.checkOut = entity.getCheckOut();
        this.price = entity.getPrice();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomDescription() {
        return roomDescription;
    }

    public void setRoomDescription(String roomDescription) {
        this.roomDescription = roomDescription;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StayResponseDTO that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
