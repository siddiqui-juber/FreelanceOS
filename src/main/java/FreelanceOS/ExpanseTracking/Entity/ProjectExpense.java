package FreelanceOS.ExpanseTracking.Entity;


import FreelanceOS.ExpanseTracking.Enums.ExpenseCategory;
import FreelanceOS.ProjectManagement.Entity.Project;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "project_expenses")
public class ProjectExpense {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private String expenseTitle;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    private String currency;

    @Column(length = 3000)
    private String description;

    private LocalDate expenseDate;

    @Enumerated(EnumType.STRING)
    private ExpenseCategory category;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
