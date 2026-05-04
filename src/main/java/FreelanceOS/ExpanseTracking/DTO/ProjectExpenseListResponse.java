package FreelanceOS.ExpanseTracking.DTO;


import FreelanceOS.ExpanseTracking.Entity.ProjectExpense;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectExpenseListResponse {

    private List<ProjectExpenseResponse> expenses;
    private BigDecimal totalExpenses;
}