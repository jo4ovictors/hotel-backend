package br.edu.ifmg.hotelbao.dtos;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceDTO {

    private UserInvoiceDTO user;
    private List<StayItemDTO> stays;
    private BigDecimal total;

    public InvoiceDTO(UserInvoiceDTO user, List<StayItemDTO> stays, BigDecimal total) {
        this.user = user;
        this.stays = stays;
        this.total = total;
    }

    public InvoiceDTO(){}

    public UserInvoiceDTO getUser() {
        return user;
    }

    public void setUser(UserInvoiceDTO user) {
        this.user = user;
    }

    public List<StayItemDTO> getStays() {
        return stays;
    }

    public void setStays(List<StayItemDTO> stays) {
        this.stays = stays;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
