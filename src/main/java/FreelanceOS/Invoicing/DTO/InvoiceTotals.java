package FreelanceOS.Invoicing.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class InvoiceTotals {

    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal taxable;
    private BigDecimal tax;
    private BigDecimal total;
}