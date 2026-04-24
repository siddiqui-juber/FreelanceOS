package FreelanceOS.ClientManagement.Repository;

import FreelanceOS.ClientManagement.Entity.ClientCommunication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientCommunicationRepository extends JpaRepository<ClientCommunication, UUID> {

    List<ClientCommunication>findByClientIdOrderByCommunicationDateDesc(UUID clientId);

    Optional<ClientCommunication> findByIdAndClientId(UUID id, UUID clientId);
}
