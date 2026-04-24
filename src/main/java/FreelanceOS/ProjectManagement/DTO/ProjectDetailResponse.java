package FreelanceOS.ProjectManagement.DTO;

import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import FreelanceOS.ProjectManagement.Enums.ProjectType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ProjectDetailResponse {

    private UUID id;
    private UUID clientId;
    private String projectName;
    private ProjectType projectType;
    private ProjectStatus status;

    private LocalDate startDate;
    private LocalDateTime createdAt;

    private List<ProjectStageResponse> stages;


    private long totalTasks;
    private long completedTasks;

    private long totalDeliverables;


    private double budget;
    private double totalExpenses;
    private double totalInvoiced;
    private double totalPaid;
    private double outstanding;
    private double netProfit;
    private double profitMargin;
}
