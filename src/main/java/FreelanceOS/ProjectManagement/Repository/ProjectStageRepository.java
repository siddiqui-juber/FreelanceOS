package FreelanceOS.ProjectManagement.Repository;

import FreelanceOS.ProjectManagement.Entity.ProjectStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectStageRepository extends JpaRepository<ProjectStage, UUID> {

    long countByProjectId(UUID projectId);

    List<ProjectStage> findByProjectId(UUID projectId);

    List<ProjectStage> findByProjectIdOrderByStageOrder(UUID projectId);

    Optional<ProjectStage> findByIdAndProject_Client_UserId(UUID stageId, UUID userId);
}