package FreelanceOS.ClientManagement.Repository;

import FreelanceOS.ClientManagement.Entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    List<Client> findByUserId(UUID userId);
    Optional<Client> findByIdAndUserId(UUID id,UUID userId);

    boolean existsByEmailAndUserId(String email, UUID userId);


}

