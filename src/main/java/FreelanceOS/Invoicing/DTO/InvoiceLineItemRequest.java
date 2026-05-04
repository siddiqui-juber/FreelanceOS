package FreelanceOS.Invoicing.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceLineItemRequest {

    @NotBlank(message = "description is required")
    private String description;

    private BigDecimal lineTotal;

    @NotNull(message = "quantity is required")
    private BigDecimal quantity;

    @NotNull(message = "unitRate is required")
    private BigDecimal unitRate;
}