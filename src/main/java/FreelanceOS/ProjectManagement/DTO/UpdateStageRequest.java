package FreelanceOS.ProjectManagement.DTO;

import FreelanceOS.ProjectManagement.Enums.StageStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateStageRequest {


    private String stageName;
    private StageStatus status;
    private LocalDate startDate;
    private LocalDate completionDate;
}
