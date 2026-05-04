package FreelanceOS.TaskManagement.Repository;

import FreelanceOS.TaskManagement.Entity.ProjectTask;
import FreelanceOS.TaskManagement.Enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<ProjectTask, UUID> {

    long countByProjectId(UUID projectId);

    long countByProjectIdAndStatus(UUID projectId, TaskStatus status);

    Optional<ProjectTask> findByIdAndProject_Client_UserId(UUID taskId, UUID userId);

    List<ProjectTask> findByProjectId(UUID projectId);

    List<ProjectTask> findByProjectIdAndStageId(UUID projectId, UUID stageId);

    List<ProjectTask> findByProjectIdAndStatus(UUID projectId, TaskStatus status);
}