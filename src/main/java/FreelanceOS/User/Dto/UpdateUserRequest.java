package FreelanceOS.User.Dto;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String businessName;
    private String phoneNumber;
    private String defaultCurrency;
    private String timeZone;
}
