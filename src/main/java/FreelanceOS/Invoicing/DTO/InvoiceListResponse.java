package FreelanceOS.Invoicing.DTO;


import FreelanceOS.Invoicing.Enums.InvoiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
public class InvoiceListResponse {

    private UUID id;
    private String invoiceNumber;

    private UUID clientId;
    private UUID projectId;

    private InvoiceStatus status;

    private LocalDate issueDate;
    private LocalDate dueDate;

    private BigDecimal totalAmount;
    private BigDecimal totalPaid;

    private boolean isOverdue;
}
