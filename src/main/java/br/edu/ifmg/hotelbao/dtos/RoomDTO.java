package br.edu.ifmg.hotelbao.dtos;

import br.edu.ifmg.hotelbao.entities.Room;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.util.Objects;

@Schema(name = "RoomDTO", description = "Data Transfer Object for Room entity containing information such as description, price and image URL.")
public class RoomDTO extends RepresentationModel<RoomDTO> {

    @Schema(description = "Unique identifier of the room", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Detailed textual description of the room", example = "Deluxe room with ocean view.")
    private String description;

    @Schema(description = "Daily price for booking the room", example = "199.99", type = "number", format = "bigdecimal")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal price;

    @Schema(description = "URL of the image representing the room", example = "https://example.com/images/room.jpg")
    private String imageUrl;

    public RoomDTO() {
    }

    public RoomDTO(Long id, String description, BigDecimal price, String imageUrl) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public RoomDTO(Room entity) {
        this.id = entity.getId();
        this.description = entity.getDescription();
        this.price = entity.getPrice();
        this.imageUrl = entity.getImageUrl();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RoomDTO roomDTO)) return false;
        return Objects.equals(id, roomDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}