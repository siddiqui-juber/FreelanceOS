package FreelanceOS.User.Controller;

import FreelanceOS.Security.UserPrincipal;
import FreelanceOS.User.Dto.ChangePasswordRequest;
import FreelanceOS.User.Dto.UpdateUserRequest;
import FreelanceOS.User.Dto.UserResponse;
import FreelanceOS.User.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "User API", description = "Manage authenticated user profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET CURRENT USER
    @Operation(summary = "Get authenticated user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile fetched",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(
                userService.getCurrentUser(user.getId())
        );
    }

    //  UPDATE PROFILE
    @Operation(summary = "Update user profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/me")
    public ResponseEntity<UserResponse> update(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(
                userService.updateProfile(user.getId(), request)
        );
    }

    //CHANGE PASSWORD
    @Operation(summary = "Change user password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Incorrect current password"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/me/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(user.getId(), request);

        return ResponseEntity.ok(Map.of(
                "message", "Password updated successfully"
        ));
    }
}