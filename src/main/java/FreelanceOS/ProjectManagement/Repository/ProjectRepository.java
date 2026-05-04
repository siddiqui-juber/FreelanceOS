package FreelanceOS.ProjectManagement.Repository;

import FreelanceOS.ProjectManagement.Entity.Project;
import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Optional<Project> findByIdAndClient_UserId(UUID projectId, UUID userId);

    List<Project> findByClient_UserId(UUID userId);

    boolean existsByClient_IdAndClient_UserIdAndStatusNotIn(
            UUID clientId,
            UUID userId,
            List<ProjectStatus> statuses
    );

    @Query("""
    SELECT p FROM Project p
    JOIN FETCH p.client
    LEFT JOIN FETCH p.stages
    WHERE p.id = :projectId AND p.client.user.id = :userId
""")
    Optional<Project> findProjectWithClientAndStages(UUID projectId, UUID userId);

    long countByClientId(UUID clientId);

    long countByClientIdAndStatus(UUID clientId, ProjectStatus status);
}
