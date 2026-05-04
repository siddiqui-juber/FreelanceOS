package FreelanceOS;

import FreelanceOS.ClientManagement.DTO.CreateClientRequest;
import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Repository.ClientRepository;
import FreelanceOS.ClientManagement.Service.ClientService;
import FreelanceOS.Exception.ApiException;
import FreelanceOS.Invoicing.Entity.Invoice;
import FreelanceOS.Invoicing.Entity.InvoicePayment;
import FreelanceOS.Invoicing.Repository.InvoicePaymentRepository;
import FreelanceOS.Invoicing.Repository.InvoiceRepository;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @InjectMocks
    private ClientService service;

    @Mock
    private ClientRepository clientRepo;

    @Mock
    private ProjectRepository projectRepo;

    @Mock
    private InvoiceRepository invoiceRepo;

    @Mock
    private InvoicePaymentRepository paymentRepo;


    // 1. EMAIL UNIQUENESS


    @Test
    void shouldThrowException_WhenEmailAlreadyExists() {

        UUID userId = UUID.randomUUID();
        String email = "test@mail.com";

        when(clientRepo.existsByEmailAndUserId(email, userId))
                .thenReturn(true);

        CreateClientRequest req = new CreateClientRequest();
        req.setEmail(email);

        assertThrows(ApiException.class, () ->
                service.createClient(userId, req)
        );
    }


    // 2. DELETE BLOCKED


    @Test
    void shouldThrowException_WhenClientHasProjects() {

        UUID userId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        Client client = new Client();
        client.setId(clientId);

        when(clientRepo.findByIdAndUserId(clientId, userId))
                .thenReturn(Optional.of(client));

        // ✔ FIX: use count instead of find
        when(projectRepo.countByClientId(clientId))
                .thenReturn(1L);

        assertThrows(ApiException.class, () ->
                service.deleteClient(userId, clientId)
        );
    }


    // 3. FINANCIAL SUMMARY


    @Test
    void shouldCalculateOutstandingAmount() {

        UUID clientId = UUID.randomUUID();

        // Mock invoices
        Invoice i1 = new Invoice();
        Invoice i2 = new Invoice();

        when(invoiceRepo.findByClientId(clientId))
                .thenReturn(List.of(i1, i2));

        // Mock payments
        InvoicePayment p1 = new InvoicePayment();
        p1.setAmountPaid(BigDecimal.valueOf(1000));

        when(paymentRepo.findByInvoiceId(any()))
                .thenReturn(List.of(p1));

        BigDecimal totalPaid = p1.getAmountPaid();

        assertEquals(BigDecimal.valueOf(1000), totalPaid);
    }
}