package FreelanceOS.TaskManagement.DTO;


import FreelanceOS.TaskManagement.Enums.TaskPriority;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateTaskRequest {

    private UUID stageId;

    private String taskTitle;

    private String description;

    private TaskPriority priority;

    private LocalDate dueDate;
}
