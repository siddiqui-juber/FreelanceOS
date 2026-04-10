package FreelanceOS.User.Controller;

import FreelanceOS.User.Dto.ChangePasswordRequest;
import FreelanceOS.User.Dto.UpdateUserRequest;
import FreelanceOS.User.Dto.UserResponse;
import FreelanceOS.User.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private String extractToken(String header) {
        return header.replace("Bearer ", "").trim();
    }



    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@RequestHeader("Authorization") String header) {

        String token = extractToken(header);

        return ResponseEntity.ok(userService.getCurrentUser(token));
    }


    @PutMapping("/me")
    public ResponseEntity<UserResponse> update(
            @RequestHeader("Authorization") String header,
            @RequestBody UpdateUserRequest request) {

        String token = extractToken(header);

        return ResponseEntity.ok(userService.updateProfile(token, request));
    }


    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String header,
            @RequestBody ChangePasswordRequest request) {

        String token = extractToken(header);

        userService.changePassword(token, request);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }
}