package FreelanceOS.User.Service;

import FreelanceOS.SecurityConfig.JwtUtil;
import FreelanceOS.User.Dto.ChangePasswordRequest;
import FreelanceOS.User.Dto.UpdateUserRequest;
import FreelanceOS.User.Dto.UserResponse;
import FreelanceOS.User.Entity.User;
import FreelanceOS.User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final JwtUtil jwtUtil;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       JwtUtil jwtUtil,
                       BCryptPasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.jwtUtil=jwtUtil;
        this.passwordEncoder= passwordEncoder;
    }
    public UserResponse getCurrentUser(String token) {
        UUID userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToResponse(user);
    }

    public UserResponse updateProfile(String token, UpdateUserRequest request) {
        UUID userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBusinessName(request.getBusinessName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDefaultCurrency(request.getDefaultCurrency());
        user.setTimeZone(request.getTimeZone());

        userRepository.save(user);

        return mapToResponse(user);
    }

    public void changePassword(String token, ChangePasswordRequest request) {

        UUID userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid current password");
        }
        // set new password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setBusinessName(user.getBusinessName());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setDefaultCurrency(user.getDefaultCurrency());
        response.setTimezone(user.getTimeZone());

        return response;
    }






}
