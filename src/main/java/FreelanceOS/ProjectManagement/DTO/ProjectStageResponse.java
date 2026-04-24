package FreelanceOS.ProjectManagement.DTO;

import FreelanceOS.ProjectManagement.Enums.StageStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ProjectStageResponse {

    private UUID id;

    private String stageName;

    private int stageOrder;

    private StageStatus status;

    private LocalDate startDate;

    private LocalDate completionDate;
}
