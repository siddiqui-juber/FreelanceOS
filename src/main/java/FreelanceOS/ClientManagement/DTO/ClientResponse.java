package FreelanceOS.ClientManagement.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.UUID;

@Data
@Schema(description = "Client response")
public class ClientResponse {

    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    private String clientName;
    private String companyName;
    private String email;
    private String phoneNumber;
    private String status;

    private long totalProjects;
    private long activeProjects;
    private double totalRevenue;
}
