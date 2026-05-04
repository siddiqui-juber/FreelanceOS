package FreelanceOS;

import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Repository.ClientRepository;

import FreelanceOS.Exception.ApiException;
import FreelanceOS.Invoicing.DTO.InvoicePaymentRequest;
import FreelanceOS.Invoicing.DTO.InvoiceResponse;
import FreelanceOS.Invoicing.Entity.Invoice;
import FreelanceOS.Invoicing.Entity.InvoiceLineItem;
import FreelanceOS.Invoicing.Entity.InvoicePayment;
import FreelanceOS.Invoicing.Enums.InvoiceStatus;
import FreelanceOS.Invoicing.Repository.InvoiceLineItemRepository;
import FreelanceOS.Invoicing.Repository.InvoicePaymentRepository;
import FreelanceOS.Invoicing.Repository.InvoiceRepository;
import FreelanceOS.Invoicing.Service.InvoiceService;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import FreelanceOS.User.Entity.User;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;



import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
    class InvoiceServiceTest {

        @InjectMocks
        private InvoiceService service;

        @Mock
        private InvoiceRepository invoiceRepo;

        @Mock
        private InvoiceLineItemRepository itemRepo;

        @Mock
        private InvoicePaymentRepository paymentRepo;

        @Mock
        private ClientRepository clientRepo;

        @Mock
        private ProjectRepository projectRepo;

        @Test
        void shouldCalculateTotal_WithTaxAndDiscount() {

            UUID invoiceId = UUID.randomUUID();

            Invoice invoice = new Invoice();
            invoice.setId(invoiceId);
            invoice.setDiscountAmount(BigDecimal.valueOf(500));
            invoice.setTaxRate(BigDecimal.valueOf(0.5));

            when(invoiceRepo.findById(invoiceId)).thenReturn(Optional.of(invoice));

            InvoiceLineItem item = new InvoiceLineItem();
            item.setQuantity(BigDecimal.valueOf(3));
            item.setUnitRate(BigDecimal.valueOf(500));

            when(itemRepo.findByInvoiceId(invoiceId)).thenReturn(List.of(item));
            when(paymentRepo.findByInvoiceId(invoiceId)).thenReturn(List.of());

            InvoiceResponse res = service.get(UUID.randomUUID(), invoiceId);

            assertEquals(BigDecimal.valueOf(1500), res.getSubtotal());
            assertEquals(BigDecimal.valueOf(1000), res.getTaxableAmount());
            assertEquals(BigDecimal.valueOf(500), res.getTaxAmount());
            assertEquals(BigDecimal.valueOf(1500), res.getTotalAmount());
        }


    @Test
    void shouldCalculateTotal_WithZeroTax() {

        Invoice invoice = new Invoice();
        invoice.setTaxRate(BigDecimal.ZERO);
        invoice.setDiscountAmount(BigDecimal.ZERO);

        InvoiceLineItem item = new InvoiceLineItem();
        item.setQuantity(BigDecimal.valueOf(2));
        item.setUnitRate(BigDecimal.valueOf(100));

        when(invoiceRepo.findById(any())).thenReturn(Optional.of(invoice));
        when(itemRepo.findByInvoiceId(any())).thenReturn(List.of(item));
        when(paymentRepo.findByInvoiceId(any())).thenReturn(List.of());

        InvoiceResponse res = service.get(UUID.randomUUID(), UUID.randomUUID());

        assertEquals(BigDecimal.valueOf(200), res.getTotalAmount());
    }

    @Test
    void shouldHandleNegativeTaxableAmount() {

        Invoice invoice = new Invoice();
        invoice.setDiscountAmount(BigDecimal.valueOf(5000)); // huge discount
        invoice.setTaxRate(BigDecimal.valueOf(0.2));

        InvoiceLineItem item = new InvoiceLineItem();
        item.setQuantity(BigDecimal.valueOf(1));
        item.setUnitRate(BigDecimal.valueOf(1000));

        when(invoiceRepo.findById(any())).thenReturn(Optional.of(invoice));
        when(itemRepo.findByInvoiceId(any())).thenReturn(List.of(item));
        when(paymentRepo.findByInvoiceId(any())).thenReturn(List.of());

        InvoiceResponse res = service.get(UUID.randomUUID(), UUID.randomUUID());

        assertEquals(BigDecimal.ZERO, res.getTaxableAmount());
        assertEquals(BigDecimal.ZERO, res.getTaxAmount());
    }

    @Test
    void shouldThrowException_WhenInvalidTransition() {

        UUID userId = UUID.randomUUID();
        UUID invoiceId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Client client = new Client();
        client.setUser(user);

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setClient(client);
        invoice.setStatus(InvoiceStatus.PAID);

        when(invoiceRepo.findById(invoiceId)).thenReturn(Optional.of(invoice));

        assertThrows(ApiException.class, () -> {
            service.updateStatus(userId, invoiceId, InvoiceStatus.SENT);
        });
    }

    @Test
    void shouldThrowException_WhenDraftWithoutItems() {

        UUID userId = UUID.randomUUID();
        UUID invoiceId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Client client = new Client();
        client.setUser(user);

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setClient(client);   // 🔥 REQUIRED
        invoice.setStatus(InvoiceStatus.DRAFT);

        when(invoiceRepo.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(itemRepo.findByInvoiceId(invoiceId)).thenReturn(List.of());

        assertThrows(ApiException.class, () -> {
            service.updateStatus(userId, invoiceId, InvoiceStatus.SENT);
        });
    }

    @Test
    void shouldMarkInvoiceAsPaid_WhenFullyPaid() {

        UUID invoiceId = UUID.randomUUID();

        Invoice invoice = new Invoice();
        invoice.setId(invoiceId);
        invoice.setStatus(InvoiceStatus.SENT);

        when(invoiceRepo.findById(invoiceId)).thenReturn(Optional.of(invoice));

        InvoicePayment payment = new InvoicePayment();
        payment.setAmountPaid(BigDecimal.valueOf(1500));

        when(paymentRepo.findByInvoiceId(invoiceId)).thenReturn(List.of(payment));

        InvoiceResponse mockResponse = new InvoiceResponse();
        mockResponse.setTotalAmount(BigDecimal.valueOf(1500));


        InvoiceService spy = Mockito.spy(service);
        doReturn(mockResponse).when(spy).get(any(), eq(invoiceId));

        spy.addPayment(UUID.randomUUID(), invoiceId, new InvoicePaymentRequest());

        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
    }

    @Test
    void shouldBeOverdue_WhenDueDatePassedAndNotPaid() {

        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now().minusDays(1));
        invoice.setStatus(InvoiceStatus.SENT);

        when(invoiceRepo.findByClient_UserId(any())).thenReturn(List.of(invoice));

        List<InvoiceResponse> result = service.getOverdue(UUID.randomUUID());

        assertFalse(result.isEmpty());
    }

    @Test
    void shouldNotBeOverdue_WhenPaid() {

        Invoice invoice = new Invoice();
        invoice.setDueDate(LocalDate.now().minusDays(1));
        invoice.setStatus(InvoiceStatus.PAID);

        when(invoiceRepo.findByClient_UserId(any())).thenReturn(List.of(invoice));

        List<InvoiceResponse> result = service.getOverdue(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

}
