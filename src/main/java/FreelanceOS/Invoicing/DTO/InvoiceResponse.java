package FreelanceOS.Invoicing.DTO;


import FreelanceOS.Invoicing.Enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class InvoiceResponse {

    private UUID id;
    private String invoiceNumber;

    private UUID clientId;
    private UUID projectId;

    private InvoiceStatus status;

    private LocalDate issueDate;
    private LocalDate dueDate;

    private BigDecimal discountAmount;
    private BigDecimal taxRate;

    private boolean isOverdue;

    private List<InvoiceLineItemResponse> items;

    private BigDecimal subtotal;
    private BigDecimal taxableAmount;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private BigDecimal totalPaid;
}
