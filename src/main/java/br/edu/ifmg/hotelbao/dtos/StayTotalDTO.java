package br.edu.ifmg.hotelbao.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

public class StayTotalDTO extends RepresentationModel<StayTotalDTO> {

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
