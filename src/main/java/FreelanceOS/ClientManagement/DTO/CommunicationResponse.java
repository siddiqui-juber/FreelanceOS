package FreelanceOS.ClientManagement.DTO;


import FreelanceOS.ClientManagement.enums.CommunicationType;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CommunicationResponse {

    private UUID id;
    private CommunicationType communicationType;
    private String subjects;
    private String content;
    private Instant communicationDate;
}
