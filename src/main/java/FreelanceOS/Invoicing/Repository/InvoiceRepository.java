package FreelanceOS.Invoicing.Repository;

import FreelanceOS.Invoicing.Entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository  extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findTopByClient_UserIdOrderByCreatedAtDesc(UUID userId);

    boolean existsByInvoiceNumberAndClient_UserId(String invoiceNumber, UUID userId);
    long countByClient_UserId(UUID userId);

    List<Invoice> findByProjectId(UUID projectId);
    List<Invoice> findByClient_UserId(UUID userId);
    List<Invoice> findByClientId(UUID clientId);
}
