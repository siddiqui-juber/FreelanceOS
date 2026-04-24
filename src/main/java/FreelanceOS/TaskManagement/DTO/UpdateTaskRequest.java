package FreelanceOS.TaskManagement.DTO;

import FreelanceOS.TaskManagement.Enums.TaskPriority;
import FreelanceOS.TaskManagement.Enums.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {

    private String taskTitle;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDate dueDate;
}
