package FreelanceOS.Authenciation.Service;

import FreelanceOS.Authenciation.DTO.*;
import FreelanceOS.Authenciation.Entity.TokenBlacklist;
import FreelanceOS.User.Entity.User;
import FreelanceOS.Authenciation.Repository.TokenBlacklistRepository;
import FreelanceOS.User.Repository.UserRepository;
import FreelanceOS.Security.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       TokenBlacklistRepository tokenBlacklistRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    //  REGISTER
    public RegisterResponse register(RegisterRequest requestDto) {

        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists"
            );
        }

        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(requestDto.getPassword()));
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setBusinessName(requestDto.getBusinessName());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setDefaultCurrency(requestDto.getDefaultCurrency());
        user.setTimeZone(requestDto.getTimeZone());

        User savedUser = userRepository.save(user);

        return mapToRegisterResponse(savedUser);
    }

    //  LOGIN
    public LoginResponse login(LoginRequest loginRequest){

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"
                ));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Invalid credentials"
            );
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return new LoginResponse(accessToken, refreshToken);
    }

    //REFRESH =
    public RefreshResponse refresh(String refreshToken) {

        refreshToken = cleanToken(refreshToken);

        //  Check blacklist first
        if (tokenBlacklistRepository.existsByToken(refreshToken)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Token is invalidated"
            );
        }

        Claims claims = jwtUtil.validateToken(refreshToken);

        UUID userId = UUID.fromString(claims.getSubject());


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        String newAccessToken = jwtUtil.generateAccessToken(
                user.getId(),
                user.getEmail()
        );

        return new RefreshResponse(newAccessToken);
    }

    //  LOGOUT
    public void logout(String refreshToken) {

        refreshToken = cleanToken(refreshToken);

        if (!tokenBlacklistRepository.existsByToken(refreshToken)) {
            TokenBlacklist token = new TokenBlacklist();
            token.setToken(refreshToken);
            tokenBlacklistRepository.save(token);
        }
    }

    // HELPERS
    private String cleanToken(String token) {
        return token == null ? "" : token.trim().replace("Bearer ", "");
    }

    private RegisterResponse mapToRegisterResponse(User user) {

        RegisterResponse response = new RegisterResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setBusinessName(user.getBusinessName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDefaultCurrency(user.getDefaultCurrency());
        response.setTimeZone(user.getTimeZone());
        response.setAccountStatus(user.getAccountStatus().name());
        response.setEmailVerified(user.isEmailVerified());
        response.setCreatedAt(user.getCreatedAt());

        return response;
    }
}