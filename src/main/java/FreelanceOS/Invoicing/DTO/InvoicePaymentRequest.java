package FreelanceOS.Invoicing.DTO;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoicePaymentRequest {

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "paymentDate is required")
    @PastOrPresent(message = "paymentDate cannot be in future")
    private LocalDate paymentDate;

    private String paymentMethod;
    private String notes;
}
