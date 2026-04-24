package FreelanceOS.ClientManagement.DTO;

import FreelanceOS.ClientManagement.enums.AcquisitionSource;
import FreelanceOS.ClientManagement.enums.ClientStatus;
import FreelanceOS.ClientManagement.enums.ClientType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateClientRequest {

    @NotBlank
    private String clientName;
    private String companyName;
    private String email;
    private String phoneNumber;
    private Address address;
    @NotNull
    private ClientType clientType;
    private AcquisitionSource acquisitionSource;
    private String acquisitionDetails;
    @NotNull
    private ClientStatus status;
    private String notes;

}
