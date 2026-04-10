package FreelanceOS.User.Dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserResponse {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String businessName;
    private String phoneNumber;
    private String defaultCurrency;
    private String timezone;

}
