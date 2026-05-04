package FreelanceOS.Invoicing.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InvoiceLineItemResponse {
    private UUID id;
    private String description;
    private BigDecimal quantity;
    private BigDecimal unitRate;
    private BigDecimal lineTotal;
}