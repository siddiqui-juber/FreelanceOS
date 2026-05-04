package FreelanceOS.ExpanseTracking.Repository;

import FreelanceOS.ExpanseTracking.Entity.ProjectExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectExpenseRepository extends JpaRepository<ProjectExpense, UUID> {
    List<ProjectExpense> findByProjectIdOrderByExpenseDateDesc(UUID projectId);

    Optional<ProjectExpense> findByIdAndProjectId(UUID id, UUID projectId);

    @Query("SELECT SUM(e.amount) FROM ProjectExpense e WHERE e.project.id = :projectId")
    BigDecimal getTotalExpensesByProjectId(UUID projectId);


    List<ProjectExpense> findByProjectId(UUID projectId);
}
