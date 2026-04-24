package FreelanceOS.ClientManagement.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;

@Schema(description = "Address details")
@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String Country;
    private String zipCode;
}
