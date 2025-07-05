package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.Stay;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Objects;

@Schema(name = "StayCreateDTO", description = "Data Transfer Object used for creating a new stay record")
public class StayCreateDTO {

    @Schema(description = "Automatically generated ID of the stay", example = "1001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull(message = "User ID must not be null")
    @Schema(description = "ID of the user making the reservation", example = "501")
    private Long userId;

    @NotNull(message = "Room ID must not be null")
    @Schema(description = "ID of the room to be reserved", example = "302")
    private Long roomId;

    @NotNull(message = "Check-in date is required")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    @Schema(description = "Check-in date for the stay", example = "2025-07-01")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required")
    @Future(message = "Check-out date must be in the future")
    @Schema(description = "Check-out date for the stay", example = "2025-07-05")
    private LocalDate checkOut;

    public StayCreateDTO() {
    }

    public StayCreateDTO(Long id, Long userId, Long roomId, LocalDate checkIn, LocalDate checkOut) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public StayCreateDTO(Stay entity) {
        this.id = entity.getId();
        this.userId = entity.getUser().getId();
        this.roomId = entity.getRoom().getId();
        this.checkIn = entity.getCheckIn();
        this.checkOut = entity.getCheckOut();
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
    public boolean equals(Object o) {
        if (!(o instanceof StayCreateDTO that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "StayCreateDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", roomId=" + roomId +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                '}';
    }

}