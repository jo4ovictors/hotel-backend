package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.Stay;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Schema(name = "StayReportDTO", description = "Data Transfer Object used for reporting stay information")
public class StayReportDTO extends RepresentationModel<StayResponseDTO> {

    @Schema(description = "Unique identifier of the stay", example = "2001")
    private Long id;

    @Schema(description = "Description of the room", example = "Executive Room with Balcony")
    private String descriptionRoom;

    @Schema(description = "Total price charged for the stay", example = "1099.99")
    private BigDecimal price;

    public StayReportDTO() {
    }

    public StayReportDTO(Long id, String descriptionRoom, BigDecimal price) {
        this.id = id;
        this.descriptionRoom = descriptionRoom;
        this.price = price;
    }

    public StayReportDTO(Stay entity) {
        this.id = entity.getId();
        this.descriptionRoom = entity.getRoom().getDescription();
        this.price = entity.getPrice();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescriptionRoom() {
        return descriptionRoom;
    }

    public void setDescriptionRoom(String descriptionRoom) {
        this.descriptionRoom = descriptionRoom;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}