package FreelanceOS.ProjectManagement.Repository;

import FreelanceOS.ProjectManagement.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    Optional<Project> findByIdAndClient_UserId(UUID projectId, UUID userId);

    List<Project> findByClient_UserId(UUID userId);


}
