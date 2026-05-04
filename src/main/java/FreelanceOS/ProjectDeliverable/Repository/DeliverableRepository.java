package FreelanceOS.ProjectDeliverable.Repository;

import FreelanceOS.ProjectDeliverable.Entity.ProjectDeliverable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface DeliverableRepository extends JpaRepository<ProjectDeliverable, UUID> {
    List<ProjectDeliverable> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
    Optional<ProjectDeliverable> findByIdAndProjectId(UUID id, UUID projectId);

    long countByProjectId(UUID projectId);
}