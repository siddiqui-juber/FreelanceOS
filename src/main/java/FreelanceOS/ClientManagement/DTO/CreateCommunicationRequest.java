package FreelanceOS.ClientManagement.DTO;

import FreelanceOS.ClientManagement.enums.CommunicationType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CreateCommunicationRequest {

    @NotNull
    private CommunicationType communicationType;
    private String subjects;
    private String content;
    private Instant communicationDate;
}
