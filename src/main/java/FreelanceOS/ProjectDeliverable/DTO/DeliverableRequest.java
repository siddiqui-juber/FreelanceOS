package FreelanceOS.ProjectDeliverable.DTO;

import FreelanceOS.ProjectDeliverable.Enums.DeliverableStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class DeliverableRequest {

    @NotBlank
    private String deliverableTitle;

    private String description;

    private DeliverableStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    private String clientFeedback;
}