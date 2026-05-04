package FreelanceOS.ExpanseTracking.DTO;

import FreelanceOS.ExpanseTracking.Enums.ExpenseCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectExpenseResponse {

    private UUID id;
    private UUID projectId;

    private String expenseTitle;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDate expenseDate;
    private ExpenseCategory category;
}