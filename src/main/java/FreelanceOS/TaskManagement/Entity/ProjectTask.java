package FreelanceOS.TaskManagement.Entity;


import FreelanceOS.ProjectManagement.Entity.Project;
import FreelanceOS.ProjectManagement.Entity.ProjectStage;
import FreelanceOS.TaskManagement.Enums.TaskPriority;
import FreelanceOS.TaskManagement.Enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ProjectTask {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Project project;

    @ManyToOne
    private ProjectStage stage;

    private String taskTitle;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    private LocalDate dueDate;

    private LocalDate completionDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
