package FreelanceOS.ClientManagement.Service;

import FreelanceOS.ClientManagement.DTO.CommunicationResponse;
import FreelanceOS.ClientManagement.DTO.CreateCommunicationRequest;
import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Entity.ClientCommunication;
import FreelanceOS.ClientManagement.Repository.ClientCommunicationRepository;
import FreelanceOS.ClientManagement.Repository.ClientRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ClientCommunicationService {

    private final ClientCommunicationRepository communicationRepository;
    private final ClientRepository clientRepository;

    public ClientCommunicationService(ClientRepository clientRepository,
                                      ClientCommunicationRepository communicationRepository) {
        this.communicationRepository = communicationRepository;
        this.clientRepository = clientRepository;
    }

    // CREATE
    public CommunicationResponse addCommunication(UUID userId,
                                                  UUID clientId,
                                                  CreateCommunicationRequest request){

        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client not found"
                ));

        if (request.getCommunicationType() == null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Communication type is required"
            );
        }

        ClientCommunication comm = new ClientCommunication();
        comm.setClient(client);
        comm.setCommunicationType(request.getCommunicationType());
        comm.setSubjects(request.getSubjects());
        comm.setContent(request.getContent());
        comm.setCommunicationDate(request.getCommunicationDate());

        return mapToResponse(communicationRepository.save(comm));
    }

    //  GET
    public List<CommunicationResponse> getCommunication(UUID userId, UUID clientId){

        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client not found"
                ));

        return communicationRepository
                .findByClientIdOrderByCommunicationDateDesc(client.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // DELETE
    public void deleteCommunication(UUID userId, UUID clientId, UUID commId) {

        Client client = clientRepository.findByIdAndUserId(clientId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client not found"
                ));

        ClientCommunication comm = communicationRepository
                .findByIdAndClientId(commId, client.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Communication not found"
                ));

        communicationRepository.delete(comm);
    }

    //  MAPPER
    private CommunicationResponse mapToResponse(ClientCommunication comm){

        CommunicationResponse res = new CommunicationResponse();
        res.setId(comm.getId());
        res.setCommunicationType(comm.getCommunicationType());
        res.setSubjects(comm.getSubjects());
        res.setContent(comm.getContent());
        res.setCommunicationDate(comm.getCommunicationDate());

        return res;
    }
}