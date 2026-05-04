package FreelanceOS.Authenciation.Controller;


import FreelanceOS.Authenciation.DTO.*;
import FreelanceOS.Authenciation.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;



@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth API", description = "Authentication and authorization APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    //  REGISTER
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = RegisterResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest requestDto
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(requestDto));
    }

    // LOGIN
    @Operation(summary = "Login user and return access & refresh tokens")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest loginRequest
    ){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    //  REFRESH
    @Operation(summary = "Generate new access token using refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "New access token generated",
                    content = @Content(schema = @Schema(implementation = RefreshResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @RequestBody RefreshRequest request
    ){
        return ResponseEntity.ok(
                authService.refresh(request.getRefreshToken())
        );
    }

    // LOGOUT
    @Operation(summary = "Logout user and blacklist refresh token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid token")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Valid @RequestBody RefreshRequest request
    ){
        authService.logout(request.getRefreshToken());

        return ResponseEntity.ok(Map.of(
                "message", "Logged out successfully"
        ));
    }
}