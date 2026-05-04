package FreelanceOS.ExpanseTracking.Service;

import FreelanceOS.ExpanseTracking.DTO.ProjectExpenseListResponse;
import FreelanceOS.ExpanseTracking.DTO.ProjectExpenseRequest;
import FreelanceOS.ExpanseTracking.DTO.ProjectExpenseResponse;
import FreelanceOS.ExpanseTracking.Entity.ProjectExpense;
import FreelanceOS.ExpanseTracking.Repository.ProjectExpenseRepository;
import FreelanceOS.ProjectManagement.Entity.Project;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Service
public class ProjectExpenseService {

    private final ProjectExpenseRepository expenseRepository;
    private final ProjectRepository projectRepository;

    public ProjectExpenseService(ProjectExpenseRepository expenseRepository,
                                 ProjectRepository projectRepository) {
        this.expenseRepository = expenseRepository;
        this.projectRepository = projectRepository;
    }

    //  CREATE
    public ProjectExpenseResponse createExpense(UUID userId, UUID projectId, ProjectExpenseRequest request) {

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        ProjectExpense expense = new ProjectExpense();
        expense.setProject(project);
        expense.setExpenseTitle(request.getExpenseTitle());
        expense.setAmount(request.getAmount());
        expense.setCurrency(request.getCurrency());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setCategory(request.getCategory());
        expense.setCreatedAt(LocalDateTime.now());

        return mapToResponse(expenseRepository.save(expense));
    }

    //  GET
    public ProjectExpenseListResponse getProjectExpenses(UUID userId, UUID projectId) {

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        List<ProjectExpense> expenses =
                expenseRepository.findByProjectIdOrderByExpenseDateDesc(projectId);

        BigDecimal total = expenseRepository
                .getTotalExpensesByProjectId(projectId);

        if (total == null) {
            total = BigDecimal.ZERO;
        }

        List<ProjectExpenseResponse> responseList = expenses.stream()
                .map(this::mapToResponse)
                .toList();

        return new ProjectExpenseListResponse(responseList, total);
    }

    //  UPDATE
    public ProjectExpenseResponse updateExpense(UUID userId,
                                                UUID projectId,
                                                UUID expenseId,
                                                ProjectExpenseRequest request) {

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        ProjectExpense expense = expenseRepository
                .findByIdAndProjectId(expenseId, project.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expense not found"
                ));

        expense.setExpenseTitle(request.getExpenseTitle());
        expense.setAmount(request.getAmount());
        expense.setCurrency(request.getCurrency());
        expense.setDescription(request.getDescription());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setCategory(request.getCategory());

        ProjectExpense saved = expenseRepository.save(expense);

        return mapToResponse(saved);
    }

    // DELETE
    public void deleteExpense(UUID userId, UUID projectId, UUID expenseId) {

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        ProjectExpense expense = expenseRepository
                .findByIdAndProjectId(expenseId, project.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Expense not found"
                ));

        expenseRepository.delete(expense);
    }

private ProjectExpenseResponse mapToResponse(ProjectExpense e) {

    ProjectExpenseResponse res = new ProjectExpenseResponse();

    res.setId(e.getId());
    res.setProjectId(e.getProject().getId());
    res.setExpenseTitle(e.getExpenseTitle());
    res.setAmount(e.getAmount());
    res.setCurrency(e.getCurrency());
    res.setDescription(e.getDescription());
    res.setExpenseDate(e.getExpenseDate());
    res.setCategory(e.getCategory());

    return res;
}
}