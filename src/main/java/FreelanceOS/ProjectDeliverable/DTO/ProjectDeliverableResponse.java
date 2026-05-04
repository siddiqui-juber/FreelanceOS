package FreelanceOS.ProjectDeliverable.DTO;

import FreelanceOS.ProjectDeliverable.Enums.DeliverableStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ProjectDeliverableResponse {

    private UUID id;

    private UUID projectId;

    private String deliverableTitle;

    private String description;

    private DeliverableStatus status;   // enum as String

    private LocalDate dueDate;

    private LocalDate deliveryDate;

    private String clientFeedback;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}