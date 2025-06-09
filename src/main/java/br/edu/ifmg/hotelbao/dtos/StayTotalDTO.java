package br.edu.ifmg.hotelbao.dtos;

import java.math.BigDecimal;

public class StayTotalDTO {

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

    @Override
    public String toString() {
        return "StayTotalDTO{" +
                "total=" + total +
                '}';
    }

}
