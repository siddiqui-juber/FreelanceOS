package FreelanceOS.Invoicing.Service;

import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Repository.ClientRepository;
import FreelanceOS.Exception.ApiException;
import FreelanceOS.Invoicing.DTO.*;
import FreelanceOS.Invoicing.Entity.Invoice;
import FreelanceOS.Invoicing.Entity.InvoiceLineItem;
import FreelanceOS.Invoicing.Entity.InvoicePayment;
import FreelanceOS.Invoicing.Enums.InvoiceStatus;
import FreelanceOS.Invoicing.Repository.InvoiceLineItemRepository;
import FreelanceOS.Invoicing.Repository.InvoicePaymentRepository;
import FreelanceOS.Invoicing.Repository.InvoiceRepository;
import FreelanceOS.ProjectManagement.Entity.Project;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepo;
    private final InvoiceLineItemRepository itemRepo;
    private final InvoicePaymentRepository paymentRepo;
    private final ClientRepository clientRepo;
    private final ProjectRepository projectRepo;

    public InvoiceService(
            InvoiceRepository invoiceRepo,
            InvoiceLineItemRepository itemRepo,
            InvoicePaymentRepository paymentRepo,
            ClientRepository clientRepo,
            ProjectRepository projectRepo
    ) {
        this.invoiceRepo = invoiceRepo;
        this.itemRepo = itemRepo;
        this.paymentRepo = paymentRepo;
        this.clientRepo = clientRepo;
        this.projectRepo = projectRepo;
    }

    //CREATE
    public InvoiceResponse create(UUID userId, CreateInvoiceRequest req) {

        Client client = clientRepo.findByIdAndUserId(req.getClientId(), userId)
                .orElseThrow(() -> ApiException.notFound("Client not found"));

        Project project = null;
        if (req.getProjectId() != null) {
            project = projectRepo.findByIdAndClient_UserId(req.getProjectId(), userId)
                    .orElseThrow(() -> ApiException.notFound("Project not found"));
        }

        LocalDate issueDate = req.getIssueDate() != null ? req.getIssueDate() : LocalDate.now();
        LocalDate dueDate = req.getDueDate() != null ? req.getDueDate() : issueDate.plusDays(7);

        if (dueDate.isBefore(issueDate)) {
            throw ApiException.unprocessable("Due date cannot be before issue date");
        }

        String number = (req.getInvoiceNumber() == null || req.getInvoiceNumber().isBlank())
                ? generateInvoiceNumber(userId)
                : req.getInvoiceNumber();

        if (invoiceRepo.existsByInvoiceNumberAndClient_UserId(number, userId)) {
            throw ApiException.conflict("Invoice number already exists");
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(number);
        invoice.setClient(client);
        invoice.setProject(project);
        invoice.setStatus(InvoiceStatus.DRAFT);
        invoice.setIssueDate(issueDate);
        invoice.setDueDate(dueDate);
        invoice.setDiscountAmount(Optional.ofNullable(req.getDiscountAmount()).orElse(BigDecimal.ZERO));
        invoice.setTaxRate(Optional.ofNullable(req.getTaxRate()).orElse(BigDecimal.ZERO));
        invoice.setCreatedAt(LocalDateTime.now());

        Invoice saved = invoiceRepo.save(invoice);

        return get(userId, saved.getId());
    }

    //  GET ALL
    public List<InvoiceResponse> getAll(UUID userId,
                                        InvoiceStatus status,
                                        UUID clientId,
                                        UUID projectId,
                                        String sortBy) {

        List<Invoice> invoices = invoiceRepo.findByClient_UserId(userId);

        return invoices.stream()
                .filter(i -> status == null || i.getStatus() == status)
                .filter(i -> clientId == null || i.getClient().getId().equals(clientId))
                .filter(i -> projectId == null ||
                        (i.getProject() != null && i.getProject().getId().equals(projectId)))
                .sorted((a, b) -> "date".equalsIgnoreCase(sortBy)
                        ? b.getCreatedAt().compareTo(a.getCreatedAt())
                        : b.getId().compareTo(a.getId()))
                .map(i -> get(userId, i.getId()))
                .toList();
    }

    // GET FULL
    public InvoiceResponse get(UUID userId, UUID invoiceId) {

        Invoice invoice = getUserInvoice(userId, invoiceId);

        List<InvoiceLineItem> items = itemRepo.findByInvoiceId(invoiceId);
        List<InvoicePayment> payments = paymentRepo.findByInvoiceId(invoiceId);


        BigDecimal subtotal = items.stream()
                .map(i -> i.getQuantity().multiply(i.getUnitRate()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = Optional.ofNullable(invoice.getDiscountAmount()).orElse(BigDecimal.ZERO);
        BigDecimal taxRate = Optional.ofNullable(invoice.getTaxRate()).orElse(BigDecimal.ZERO);

        BigDecimal taxable = subtotal.subtract(discount);
        if (taxable.compareTo(BigDecimal.ZERO) < 0) {
            taxable = BigDecimal.ZERO;
        }

        BigDecimal tax = taxable.multiply(taxRate);
        BigDecimal total = taxable.add(tax);

        BigDecimal paid = payments.stream()
                .map(InvoicePayment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        return mapFull(invoice, items, subtotal, taxable, tax, total, paid);
    }
    //  ADD ITEM
    public InvoiceLineItemResponse addItem(UUID userId, UUID invoiceId, InvoiceLineItemRequest req) {

        Invoice invoice = getUserInvoice(userId, invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw ApiException.unprocessable("Only Draft invoices can be edited");
        }

        InvoiceLineItem item = new InvoiceLineItem();
        item.setInvoice(invoice);
        item.setDescription(req.getDescription());
        item.setQuantity(req.getQuantity());
        item.setUnitRate(req.getUnitRate());
        item.setCreatedAt(LocalDateTime.now());

        return mapItem(itemRepo.save(item));
    }

    // UPDATE ITEM
    public InvoiceLineItemResponse updateLineItem(UUID userId, UUID invoiceId, UUID itemId, InvoiceLineItemRequest req) {

        Invoice invoice = getUserInvoice(userId, invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw ApiException.unprocessable("Only Draft invoices can be edited");
        }

        InvoiceLineItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> ApiException.notFound("Item not found"));

        if (!item.getInvoice().getId().equals(invoiceId)) {
            throw ApiException.unprocessable("Item does not belong to invoice");
        }

        item.setDescription(req.getDescription());
        item.setQuantity(req.getQuantity());
        item.setUnitRate(req.getUnitRate());

        return mapItem(itemRepo.save(item));
    }

    //  DELETE ITEM
    public void deleteLineItem(UUID userId, UUID invoiceId, UUID itemId) {

        Invoice invoice = getUserInvoice(userId, invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw ApiException.unprocessable("Only Draft invoices can be edited");
        }

        InvoiceLineItem item = itemRepo.findById(itemId)
                .orElseThrow(() -> ApiException.notFound("Item not found"));

        if (!item.getInvoice().getId().equals(invoiceId)) {
            throw ApiException.unprocessable("Item does not belong to invoice");
        }

        itemRepo.delete(item);
    }

    //  STATUS
    public void updateStatus(UUID userId, UUID invoiceId, InvoiceStatus newStatus) {

        Invoice invoice = getUserInvoice(userId, invoiceId);

        if (invoice.getStatus() == InvoiceStatus.DRAFT &&
                itemRepo.findByInvoiceId(invoiceId).isEmpty()) {
            throw ApiException.unprocessable("No line items added");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID ||
                invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw ApiException.unprocessable("Final state reached");
        }

        invoice.setStatus(newStatus);
        invoiceRepo.save(invoice);
    }

    //  PAYMENT
    public InvoicePaymentResponse addPayment(UUID userId, UUID invoiceId, InvoicePaymentRequest req) {

        Invoice invoice = getUserInvoice(userId, invoiceId);

        InvoicePayment payment = new InvoicePayment();
        payment.setInvoice(invoice);
        payment.setAmountPaid(req.getAmount());
        payment.setPaymentDate(req.getPaymentDate());
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setNotes(req.getNotes());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(payment);

        BigDecimal totalPaid = paymentRepo.findByInvoiceId(invoiceId).stream()
                .map(InvoicePayment::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPaid.compareTo(get(userId, invoiceId).getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
            invoiceRepo.save(invoice);
        }

        return mapPayment(payment);
    }

    // DELETE PAYMENT
    public void deletePayment(UUID userId, UUID invoiceId, UUID paymentId) {

        InvoicePayment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> ApiException.notFound("Payment not found"));

        if (!payment.getInvoice().getId().equals(invoiceId)) {
            throw ApiException.unprocessable("Payment does not belong to invoice");
        }

        paymentRepo.delete(payment);
    }

    //  OVERDUE
    public List<InvoiceResponse> getOverdue(UUID userId) {

        return invoiceRepo.findByClient_UserId(userId).stream()
                .filter(i -> i.getDueDate() != null)
                .filter(i -> i.getDueDate().isBefore(LocalDate.now()))
                .filter(i -> i.getStatus() != InvoiceStatus.PAID)
                .map(this::map)
                .toList();
    }

    //  DELETE INVOICE
    public void deleteInvoice(UUID userId, UUID invoiceId) {

        Invoice invoice = getUserInvoice(userId, invoiceId);

        if (invoice.getStatus() != InvoiceStatus.DRAFT &&
                invoice.getStatus() != InvoiceStatus.CANCELLED) {
            throw ApiException.unprocessable("Only Draft or Cancelled invoices can be deleted");
        }

        if (!paymentRepo.findByInvoiceId(invoiceId).isEmpty()) {
            throw ApiException.unprocessable("Cannot delete invoice with payments");
        }

        invoiceRepo.delete(invoice);
    }

    //  HELPERS 

    private Invoice getUserInvoice(UUID userId, UUID id) {
        return invoiceRepo.findById(id)
                .filter(i -> i.getClient().getUser().getId().equals(userId))
                .orElseThrow(() -> ApiException.notFound("Invoice not found"));
    }

    private String generateInvoiceNumber(UUID userId) {
        long count = invoiceRepo.countByClient_UserId(userId) + 1;
        return String.format("INV-%04d", count);
    }

    private InvoiceResponse map(Invoice i) {
        InvoiceResponse r = new InvoiceResponse();
        r.setId(i.getId());
        r.setInvoiceNumber(i.getInvoiceNumber());
        r.setStatus(i.getStatus());
        r.setClientId(i.getClient().getId());
        r.setProjectId(i.getProject() != null ? i.getProject().getId() : null);
        r.setIssueDate(i.getIssueDate());
        r.setDueDate(i.getDueDate());
        r.setDiscountAmount(i.getDiscountAmount());
        r.setTaxRate(i.getTaxRate());
        return r;
    }

    private InvoiceLineItemResponse mapItem(InvoiceLineItem i) {
        return new InvoiceLineItemResponse(
                i.getId(),
                i.getDescription(),
                i.getQuantity(),
                i.getUnitRate(),
                i.getQuantity().multiply(i.getUnitRate())
        );
    }

    private InvoicePaymentResponse mapPayment(InvoicePayment p) {
        InvoicePaymentResponse r = new InvoicePaymentResponse();
        r.setId(p.getId());
        r.setAmountPaid(p.getAmountPaid());
        r.setPaymentDate(p.getPaymentDate());
        r.setPaymentMethod(p.getPaymentMethod());
        r.setNotes(p.getNotes());
        return r;
    }

    private InvoiceResponse mapFull(
            Invoice invoice,
            List<InvoiceLineItem> items,
            BigDecimal subtotal,
            BigDecimal taxable,
            BigDecimal tax,
            BigDecimal total,
            BigDecimal paid
    ) {
        InvoiceResponse r = map(invoice);
        r.setItems(items.stream().map(this::mapItem).toList());
        r.setSubtotal(subtotal);
        r.setTaxableAmount(taxable);
        r.setTaxAmount(tax);
        r.setTotalAmount(total);
        r.setTotalPaid(paid);

        boolean overdue = invoice.getDueDate() != null &&
                invoice.getDueDate().isBefore(LocalDate.now()) &&
                (invoice.getStatus() == InvoiceStatus.SENT ||
                        invoice.getStatus() == InvoiceStatus.VIEWED);

        r.setOverdue(overdue);
        return r;
    }
}