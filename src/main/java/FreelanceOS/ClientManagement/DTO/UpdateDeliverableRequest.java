package FreelanceOS.ClientManagement.DTO;


import FreelanceOS.ProjectDeliverable.Enums.DeliverableStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateDeliverableRequest {

    private DeliverableStatus status;

    private String clientFeedback;

    private LocalDate deliveryDate;
}
