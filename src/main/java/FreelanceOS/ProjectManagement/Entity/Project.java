package FreelanceOS.ProjectManagement.Entity;


import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import FreelanceOS.ProjectManagement.Enums.ProjectType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class Project {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Client client;

    private String ProjectName;

    @Enumerated(EnumType.STRING)
    private ProjectType projectType;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private LocalDate startDate;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectStage> stages;

}
