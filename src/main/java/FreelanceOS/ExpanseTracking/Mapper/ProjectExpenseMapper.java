package FreelanceOS.ExpanseTracking.Mapper;

import FreelanceOS.ExpanseTracking.DTO.ProjectExpenseResponse;
import FreelanceOS.ExpanseTracking.Entity.ProjectExpense;

import java.util.UUID;

public class ProjectExpenseMapper {

    public static ProjectExpenseResponse toResponse(ProjectExpense e) {

        UUID projectId = null;

        if (e.getProject() != null) {
            projectId = e.getProject().getId();
        }

        return new ProjectExpenseResponse(
                e.getId(),
                projectId,
                e.getExpenseTitle(),
                e.getAmount(),
                e.getCurrency(),
                e.getDescription(),
                e.getExpenseDate(),
                e.getCategory()
        );
    }
}