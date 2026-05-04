package FreelanceOS.ClientManagement.Service;

import FreelanceOS.ClientManagement.DTO.ClientResponse;
import FreelanceOS.ClientManagement.DTO.CreateClientRequest;
import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Repository.ClientRepository;
import FreelanceOS.ClientManagement.enums.ClientStatus;
import FreelanceOS.ClientManagement.enums.ClientType;
import FreelanceOS.Invoicing.Repository.InvoiceLineItemRepository;
import FreelanceOS.Invoicing.Repository.InvoiceRepository;
import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import FreelanceOS.User.Entity.User;
import FreelanceOS.User.Repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;


@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final InvoiceLineItemRepository invoiceLineItemRepository;

    public ClientService(ClientRepository clientRepository,
                         UserRepository userRepository,
                         ProjectRepository projectRepository,
                         InvoiceLineItemRepository invoiceLineItemRepository) {
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.invoiceLineItemRepository=invoiceLineItemRepository;
    }

    //  CREATE
    public ClientResponse createClient(UUID userId, CreateClientRequest request){

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        if (clientRepository.existsByEmailAndUserId(request.getEmail(), userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists"
            );
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

    //GET ALL
    public List<ClientResponse> getClients(UUID userId,
                                           ClientStatus status,
                                           ClientType clientType,
                                           String sortBy){

        List<Client> clients = clientRepository.findByUserId(userId);

        if (status != null){
            clients = clients.stream()
                    .filter(c -> c.getStatus() == status)
                    .toList();
        }

        if (clientType != null){
            clients = clients.stream()
                    .filter(c -> c.getClientType() == clientType)
                    .toList();
        }

        if ("name".equalsIgnoreCase(sortBy)){
            clients.sort(Comparator.comparing(Client::getClientName));
        } else if ("created_at".equalsIgnoreCase(sortBy)) {
            clients.sort(Comparator.comparing(Client::getCreatedAt));
        }

        return clients.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= GET BY ID =================
    public ClientResponse getClientById(UUID userId, UUID clientId){

        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client not found"
                ));

        return mapToResponse(client);
    }

    //  UPDATE
    public ClientResponse updateClient(UUID userId, UUID clientId, CreateClientRequest request){

        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client not found"
                ));

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

    //  DELETE
    public void deleteClient(UUID userId, UUID clientId) {

        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client not found"
                ));

        boolean hasActiveProjects =
                projectRepository.existsByClient_IdAndClient_UserIdAndStatusNotIn(
                        clientId,
                        userId,
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

    //  MAPPER
    private ClientResponse mapToResponse(Client client) {

        ClientResponse response = new ClientResponse();

        response.setId(client.getId());
        response.setClientName(client.getClientName());
        response.setCompanyName(client.getCompanyName());
        response.setEmail(client.getEmail());
        response.setPhoneNumber(client.getPhoneNumber());
        response.setStatus(
                client.getStatus() != null ? client.getStatus().name() : null
        );

        long totalProjects = projectRepository.countByClientId(client.getId());

        long activeProjects = projectRepository
                .countByClientIdAndStatus(client.getId(), ProjectStatus.ACTIVE);

        double totalRevenue = invoiceLineItemRepository
                .getTotalRevenueByClient(client.getId())
                .doubleValue();

        response.setTotalProjects((int) totalProjects);
        response.setActiveProjects((int) activeProjects);
        response.setTotalRevenue(totalRevenue);

        return response;
    }
}