package FreelanceOS.ExpanseTracking.DTO;


import FreelanceOS.ExpanseTracking.Enums.ExpenseCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ProjectExpenseRequest {

    @NotBlank
    private String expenseTitle;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotBlank
    private String currency;

    private String description;

    @NotNull
    private LocalDate expenseDate;

    @NotNull
    private ExpenseCategory category;
}