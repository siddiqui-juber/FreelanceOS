package FreelanceOS;

import FreelanceOS.ClientManagement.DTO.CreateClientRequest;
import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Repository.ClientRepository;
import FreelanceOS.ClientManagement.Service.ClientService;
import FreelanceOS.SecurityConfig.JwtUtil;
import FreelanceOS.User.Entity.User;
import FreelanceOS.User.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ClientService clientService;

    private final String TOKEN="test-token";

    // Email uniqueness per user
    @Test
    void shouldThrowException_whenEmailAlreadyExistsForUser() {

        UUID userId = UUID.randomUUID();

        CreateClientRequest request = new CreateClientRequest();
        request.setEmail("test@gmail.com");

        User user = new User();
        user.setId(userId);

        when(jwtUtil.extractUserId(TOKEN)).thenReturn(userId);

        // ✅ ADD THIS
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(clientRepository.existsByEmailAndUserId("test@gmail.com", userId))
                .thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                clientService.createClient(TOKEN, request)
        );

        assertEquals("Email already exists", ex.getMessage());
    }

    // Delete client (basic test)
    @Test
    void shouldDeleteClientSuccessfully(){
        UUID userId = UUID.randomUUID();
        UUID clientId= UUID.randomUUID();

        Client client = new Client();
        client.setId(clientId);

        when(jwtUtil.extractUserId(TOKEN)).thenReturn(userId);
        when(clientRepository.findByIdAndUserId(clientId,userId))
                .thenReturn(Optional.of(client));

        clientService.deleteClient(TOKEN,clientId);

        verify(clientRepository).delete(client);
    }

    // Client not found
    @Test
    void shouldThrowException_whenClientFound(){

        UUID userId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        when(jwtUtil.extractUserId(TOKEN)).thenReturn(userId);
        when(clientRepository.findByIdAndUserId(clientId,userId))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,()->
                clientService.deleteClient(TOKEN,clientId)
                );
        assertEquals("client not found",ex.getMessage());
    }
}
