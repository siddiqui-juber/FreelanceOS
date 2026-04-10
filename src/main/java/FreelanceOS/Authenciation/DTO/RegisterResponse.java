package FreelanceOS.Authenciation.DTO;


import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class RegisterResponse {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String businessName;
    private String phoneNumber;
    private String defaultCurrency;
    private String timeZone;
    private String accountStatus;
    private boolean emailVerified;
    private Instant createdAt;
}
