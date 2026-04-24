package FreelanceOS.ProjectManagement.DTO;

import FreelanceOS.ProjectManagement.Entity.ProjectStage;
import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import FreelanceOS.ProjectManagement.Enums.ProjectType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateProjectRequest {


        @NotNull(message = "clientId is required")
        private UUID clientId;

        @NotBlank(message = "projectName must not be blank")
        private String projectName;

        @NotNull(message = "projectType is required")
        private ProjectType projectType;

        private LocalDate startDate;


    }
