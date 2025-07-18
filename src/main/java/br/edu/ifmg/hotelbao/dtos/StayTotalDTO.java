package br.edu.ifmg.hotelbao.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

@Schema(name = "StayTotalDTO", description = "Data Transfer Object that represents the total cost of a stay")
public class StayTotalDTO extends RepresentationModel<StayTotalDTO> {

    @Schema(description = "Total cost of the stay", example = "850.00")
    private BigDecimal total;

    public StayTotalDTO() {
    }

    public StayTotalDTO(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}