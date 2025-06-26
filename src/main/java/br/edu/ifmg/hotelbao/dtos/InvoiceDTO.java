package br.edu.ifmg.hotelbao.dtos;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(name = "InvoiceDTO", description = "Data Transfer Object representing an invoice, including user details, a list of stays, and the total amount.")
public class InvoiceDTO {

    @Schema(description = "User information related to the invoice")
    private UserInvoiceDTO user;

    @ArraySchema(
            schema = @Schema(implementation = StayItemDTO.class),
            arraySchema = @Schema(description = "List of stays included in the invoice")
    )
    private List<StayItemDTO> stays;

    @Schema(description = "Total amount to be paid for the invoice", example = "499.99")
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
