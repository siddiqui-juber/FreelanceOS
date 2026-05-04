package FreelanceOS.Invoicing.DTO;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class CreateInvoiceRequest {

    @NotNull(message = "clientId is required")
    private UUID clientId;

    private UUID projectId;

    private String invoiceNumber;

    @NotNull(message = "issueDate is required")
    private LocalDate issueDate;

    @NotNull(message = "dueDate is required")
    private LocalDate dueDate;


    private BigDecimal discountAmount = BigDecimal.ZERO;

    private BigDecimal taxRate = BigDecimal.ZERO;
}
