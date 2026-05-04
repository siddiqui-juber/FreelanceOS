package FreelanceOS.Invoicing.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
public class InvoiceLineItem {


    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "invoice_id")

    private Invoice invoice;

    private String description;

    private BigDecimal quantity;
    private BigDecimal unitRate;

    private LocalDateTime createdAt;

}
