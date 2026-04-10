package FreelanceOS.Authenciation.DTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;


public class RefreshRequest {

    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}