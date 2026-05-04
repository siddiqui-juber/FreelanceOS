package FreelanceOS.ProjectDeliverable.Entity;

import FreelanceOS.ProjectDeliverable.Enums.DeliverableStatus;
import FreelanceOS.ProjectManagement.Entity.Project;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "project_deliverables")
public class ProjectDeliverable {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;


    private String deliverableTitle;
    @Column(length = 3000)
    private String description;
    @Enumerated(EnumType.STRING)
    private DeliverableStatus status = DeliverableStatus.NOT_STARTED;
    private LocalDate dueDate;
    private LocalDate deliveryDate;
    @Column(length = 3000)
    private String clientFeedback;
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