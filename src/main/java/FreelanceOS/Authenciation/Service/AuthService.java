package FreelanceOS.Authenciation.Service;


import FreelanceOS.Authenciation.DTO.*;
import FreelanceOS.Authenciation.Entity.TokenBlacklist;
import FreelanceOS.User.Entity.User;
import FreelanceOS.Authenciation.Repository.TokenBlacklistRepository;
import FreelanceOS.User.Repository.UserRepository;
import FreelanceOS.SecurityConfig.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;


    public AuthService(UserRepository userRepository, TokenBlacklistRepository tokenBlacklistRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.passwordEncoder = passwordEncoder;
       this.jwtUtil=jwtUtil;
    }


    public RegisterResponse register(@Valid RegisterRequest requestDto) {

        if(userRepository.existsByEmail(requestDto.getEmail())){
            throw new RuntimeException("Email Already exists ");
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

       //Map to register response

        RegisterResponse response = new RegisterResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setBusinessName(savedUser.getBusinessName());
        response.setPhoneNumber(savedUser.getPhoneNumber());
        response.setDefaultCurrency(savedUser.getDefaultCurrency());
        response.setTimeZone(savedUser.getTimeZone());
        response.setAccountStatus(savedUser.getAccountStatus().name());
        response.setEmailVerified(savedUser.isEmailVerified());
        response.setCreatedAt(savedUser.getCreatedAt());

        return response;
    }

    public LoginResponse login(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(),user.getPasswordHash())){
            throw new RuntimeException("Invalid credentials");
        }
        String accessToken = jwtUtil.generateAccessToken(user.getId(),user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        return new LoginResponse(accessToken , refreshToken);
    }

    public RefreshResponse refresh(String refreshToken) {

        refreshToken = refreshToken.trim().replace("Bearer ", "");

        if (tokenBlacklistRepository.existsByToken(refreshToken)) {
            throw new RuntimeException("Token is invalidated");
        }

        Claims claims = jwtUtil.validateToken(refreshToken);

        String userId = claims.getSubject();
        String email = claims.get("email", String.class);

        String newAccessToken = jwtUtil.generateAccessToken(
                UUID.fromString(userId),
                email
        );

        return new RefreshResponse(newAccessToken);
    }
    public void logout(String refreshToken) {

        TokenBlacklist token = new TokenBlacklist();
        token.setToken(refreshToken);

        tokenBlacklistRepository.save(token);
    }









    }

