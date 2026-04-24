package FreelanceOS.ClientManagement.Service;


import FreelanceOS.ClientManagement.DTO.CommunicationResponse;
import FreelanceOS.ClientManagement.DTO.CreateCommunicationRequest;
import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Entity.ClientCommunication;
import FreelanceOS.ClientManagement.Repository.ClientCommunicationRepository;
import FreelanceOS.ClientManagement.Repository.ClientRepository;
import FreelanceOS.SecurityConfig.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ClientCommunicationService {

    private final ClientCommunicationRepository communicationRepository;
    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;

    public ClientCommunicationService(ClientRepository clientRepository,
                                      ClientCommunicationRepository communicationRepository,
                                      JwtUtil jwtUtil){
        this.communicationRepository=communicationRepository;
        this.clientRepository=clientRepository;
        this.jwtUtil=jwtUtil;
    }

    public CommunicationResponse addCommunication(String token,
                                                  UUID clientId,
                                                   CreateCommunicationRequest request){
        UUID userId = jwtUtil.extractUserId(token);

        Client client = clientRepository.findByIdAndUserId(clientId,userId)
                .orElseThrow(()-> new RuntimeException("client not found"));

        if (request.getCommunicationType()== null){
            throw new RuntimeException("Communication type is required");
        }
        ClientCommunication comm = new ClientCommunication();
        comm.setClient(client);
        comm.setCommunicationType(request.getCommunicationType());
        comm.setSubjects(request.getSubjects());
        comm.setContent(request.getContent());
        comm.setCommunicationDate(request.getCommunicationDate());

        return mapToResponse(communicationRepository.save(comm));
    }
    public List<CommunicationResponse> getCommunication(String token,UUID clientId){
        UUID userId = jwtUtil.extractUserId(token);

        Client client =clientRepository.findByIdAndUserId(clientId,userId)
                .orElseThrow(()-> new RuntimeException("client not found"));
        return communicationRepository
                .findByClientIdOrderByCommunicationDateDesc(client.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public CommunicationResponse mapToResponse(ClientCommunication comm){

        CommunicationResponse res = new CommunicationResponse();
        res.setId(comm.getId());
        res.setCommunicationType(comm.getCommunicationType());
        res.setSubjects(comm.getSubjects());
        res.setContent(comm.getContent());
        res.setCommunicationDate(comm.getCommunicationDate());

        return res;
    }

    public void deleteCommunication(String token, UUID id, UUID commId) {

        UUID userId = jwtUtil.extractUserId(token);

        Client client = clientRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"user not found"));

        ClientCommunication comm = communicationRepository
                .findByIdAndClientId(commId, client.getId())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Communication not found")
                );
        communicationRepository.delete(comm);
    }

}
