package FreelanceOS.Invoicing.DTO;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class InvoicePaymentResponse {

    private UUID id;
    private BigDecimal amountPaid;

    private LocalDate paymentDate;
    private String paymentMethod;
    private String notes;
}
