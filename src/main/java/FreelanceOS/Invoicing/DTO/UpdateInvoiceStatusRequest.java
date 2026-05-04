package FreelanceOS.Invoicing.DTO;

import FreelanceOS.Invoicing.Enums.InvoiceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateInvoiceStatusRequest {

    @NotNull(message = "status is required")
    private InvoiceStatus status;
}
