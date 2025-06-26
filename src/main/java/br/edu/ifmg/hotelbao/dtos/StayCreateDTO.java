package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.Stay;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Schema(description = "Data Transfer Object used for creating a new stay record")
public class StayCreateDTO {

    @Schema(description = "Automatically generated ID of the stay", example = "1001", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "ID of the user making the reservation", example = "501")
    private Long userId;

    @Schema(description = "ID of the room to be reserved", example = "302")
    private Long roomId;

    @Schema(description = "Check-in date for the stay", example = "2025-07-01")
    private LocalDate checkIn;

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
