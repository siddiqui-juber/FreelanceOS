package FreelanceOS.Invoicing.Repository;

import FreelanceOS.Invoicing.Entity.InvoiceLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface InvoiceLineItemRepository extends JpaRepository<InvoiceLineItem, UUID> {


    @Query("""
        SELECT COALESCE(SUM(i.quantity * i.unitRate), 0)
        FROM InvoiceLineItem i
        WHERE i.invoice.client.id = :clientId
    """)
    BigDecimal getTotalRevenueByClient(UUID clientId);

    List<InvoiceLineItem> findByInvoiceId(UUID invoiceId);
}
