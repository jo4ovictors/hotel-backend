package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.Stay;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

public class StayReportDTO extends RepresentationModel<StayResponseDTO> {

    private Long id;
    private String descriptionRoom;
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
