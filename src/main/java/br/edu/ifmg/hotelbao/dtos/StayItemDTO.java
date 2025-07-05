package br.edu.ifmg.hotelbao.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(name = "StayItemDTO", description = "Data Transfer Object representing a single stay item with pricing and dates")
public class StayItemDTO {

    @Schema(description = "Description of the room or stay package", example = "Standard Room with City View")
    private String description;

    @Schema(description = "Total price for the stay", example = "499.99")
    private BigDecimal price;

    @Schema(description = "Check-in date of the stay", example = "2025-07-01")
    private LocalDate checkIn;

    @Schema(description = "Check-out date of the stay", example = "2025-07-03")
    private LocalDate checkOut;

    public StayItemDTO(String description, BigDecimal price, LocalDate checkIn, LocalDate checkOut) {
        this.description = description;
        this.price = price;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

}