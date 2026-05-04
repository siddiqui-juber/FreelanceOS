package FreelanceOS.Invoicing.Entity;


import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.Invoicing.Enums.InvoiceStatus;
import FreelanceOS.ProjectManagement.Entity.Project;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
public class Invoice {

    @Id
    @UuidGenerator
    private UUID id;

    private String invoiceNumber;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Project project;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceLineItem> items;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoicePayment> payments;

    private LocalDate issueDate;
    private LocalDate dueDate;

    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal taxRate = BigDecimal.ZERO;

    private LocalDateTime createdAt;
}