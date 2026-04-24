package FreelanceOS.TaskManagement.Repository;

import FreelanceOS.TaskManagement.Entity.ProjectTask;
import FreelanceOS.TaskManagement.Enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<ProjectTask, UUID> {

    List<ProjectTask> findByProjectId(UUID projectId);

    List<ProjectTask> findByProjectIdAndStageId(UUID projectId, UUID stageId);

    List<ProjectTask> findByProjectIdAndStatus(UUID projectId, TaskStatus status);
}