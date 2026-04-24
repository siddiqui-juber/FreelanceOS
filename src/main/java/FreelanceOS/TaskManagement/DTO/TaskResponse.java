package FreelanceOS.TaskManagement.DTO;

import FreelanceOS.TaskManagement.Enums.TaskPriority;
import FreelanceOS.TaskManagement.Enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class TaskResponse {

    private UUID id;
    private UUID stageId;
    private String taskTitle;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private LocalDate completionDate;
}
