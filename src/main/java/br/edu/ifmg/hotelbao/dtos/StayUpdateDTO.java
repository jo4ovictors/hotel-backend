package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.Stay;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Data Transfer Object used to update a stay record")
public class StayUpdateDTO {

    @Schema(description = "ID of the user associated with the stay", example = "1")
    private Long userId;

    @Schema(description = "ID of the room assigned to the stay", example = "2")
    private Long roomId;

    @Schema(description = "Check-in date of the stay", example = "2025-07-01")
    private LocalDate checkIn;

    @Schema(description = "Check-out date of the stay", example = "2025-07-05")
    private LocalDate checkOut;

    public StayUpdateDTO() {
    }

    public StayUpdateDTO(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public StayUpdateDTO(Stay entity) {
        this.userId = entity.getUser().getId();
        this.roomId = entity.getRoom().getId();
        this.checkIn = entity.getCheckIn();
        this.checkOut = entity.getCheckOut();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
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

    @Override
    public String toString() {
        return "StayCreateDTO{" +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                '}';
    }

}
