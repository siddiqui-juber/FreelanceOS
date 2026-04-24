package FreelanceOS.ProjectManagement.Entity;

import FreelanceOS.ProjectManagement.Enums.StageStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.PrimitiveIterator;
import java.util.UUID;

@Data
@Entity
public class ProjectStage {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Project project;

    private String stageName;

    private int stageOrder;

    @Enumerated(EnumType.STRING)
    private StageStatus status;

    private LocalDate startDate;

    private LocalDate completionDate;
}
