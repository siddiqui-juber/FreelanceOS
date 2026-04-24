package FreelanceOS.ClientManagement.Service;

import FreelanceOS.ClientManagement.DTO.ClientResponse;
import FreelanceOS.ClientManagement.DTO.CreateClientRequest;
import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Repository.ClientRepository;
import FreelanceOS.ClientManagement.enums.ClientStatus;
import FreelanceOS.ClientManagement.enums.ClientType;
import FreelanceOS.SecurityConfig.JwtUtil;
import FreelanceOS.User.Entity.User;
import FreelanceOS.User.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    public ClientService(ClientRepository clientRepository,
                         UserRepository userRepository,
                         JwtUtil jwtUtil
                        ){
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;

    }


    // CREATE CLIENT

    public ClientResponse createClient(String token, CreateClientRequest request){

        UUID userId = jwtUtil.extractUserId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        if (clientRepository.existsByEmailAndUserId(request.getEmail(), userId)) {
            throw new RuntimeException("Email already exists");
        }

        Client client = new Client();
        client.setUser(user);
        client.setClientName(request.getClientName());
        client.setCompanyName(request.getCompanyName());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setAddress(request.getAddress());
        client.setClientType(request.getClientType());
        client.setAcquisitionSource(request.getAcquisitionSource());
        client.setAcquisitionDetails(request.getAcquisitionDetails());
        client.setStatus(
                request.getStatus() != null ? request.getStatus() : ClientStatus.ACTIVE
        );
        client.setNotes(request.getNotes());

        Client saved = clientRepository.save(client);

        return mapToResponse(saved);
    }


    // GET ALL CLIENTS

    public List<ClientResponse> getClients(String token,
                                           ClientStatus status,
                                           ClientType clientType,
                                           String sortBy){

        UUID userId = jwtUtil.extractUserId(token);

        List<Client> clients = clientRepository.findByUserId(userId);

        if (status != null){
            clients = clients.stream()
                    .filter(c -> c.getStatus() == status)
                    .toList();
        }

        if(clientType != null){
            clients = clients.stream()
                    .filter(c -> c.getClientType() == clientType)
                    .toList();
        }

        if("name".equalsIgnoreCase(sortBy)){
            clients.sort(Comparator.comparing(Client::getClientName));
        } else if ("created_at".equalsIgnoreCase(sortBy)) {
            clients.sort(Comparator.comparing(Client::getCreatedAt));
        }

        return clients.stream()
                .map(this::mapToResponse)
                .toList();
    }


    // GET CLIENT BY ID

    public ClientResponse getClientById(String token, UUID clientId){

        UUID userId = jwtUtil.extractUserId(token);

        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        return mapToResponse(client);
    }


    // UPDATE CLIENT

    public ClientResponse updateClient(String token, UUID clientId, CreateClientRequest request){

        UUID userId = jwtUtil.extractUserId(token);

        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        client.setClientName(request.getClientName());
        client.setCompanyName(request.getCompanyName());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setAddress(request.getAddress());
        client.setClientType(request.getClientType());
        client.setAcquisitionSource(request.getAcquisitionSource());
        client.setAcquisitionDetails(request.getAcquisitionDetails());
        client.setStatus(request.getStatus());
        client.setNotes(request.getNotes());

        Client updated = clientRepository.save(client);

        return mapToResponse(updated);
    }


    // DELETE CLIENT

  /*  public void deleteClient(String token, UUID clientId) {

        UUID userId = jwtUtil.extractUserId(token);

        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        boolean hasActiveProjects =
                projectRepository.existsByClientIdAndStatusNotIn(
                        clientId,
                        List.of(ProjectStatus.COMPLETED, ProjectStatus.CANCELLED)
                );

        if (hasActiveProjects) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Client has active projects"
            );
        }

        clientRepository.delete(client);
    }
*/
  public void deleteClient(String token,UUID clientId) {
      UUID userId = jwtUtil.extractUserId(token);

      Client client = clientRepository.findByIdAndUserId(clientId, userId)
              .orElseThrow(() -> new RuntimeException("client not Foubd")
              );
      boolean hasActiveProjects = false;
      if (hasActiveProjects) {
          throw new RuntimeException("Client has active projects");
      }

      clientRepository.delete(client);
  }



    // MAPPING METHOD

    private ClientResponse mapToResponse(Client client)
    {
        long totalProjects = 0;
        long activeProjects = 0;
        double totalRevenue = 0.0;

        ClientResponse response = new ClientResponse();

        response.setId(client.getId());
        response.setClientName(client.getClientName());
        response.setCompanyName(client.getCompanyName());
        response.setEmail(client.getEmail());
        response.setPhoneNumber(client.getPhoneNumber());
        response.setStatus(
                client.getStatus() != null ? client.getStatus().name() : null
        );
        response.setTotalProjects(totalProjects);
        response.setActiveProjects(activeProjects);
        response.setTotalRevenue(totalRevenue);
        return response;

    }
}