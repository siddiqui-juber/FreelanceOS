package FreelanceOS.ExpanseTracking.Controller;

import FreelanceOS.ExpanseTracking.DTO.ProjectExpenseListResponse;
import FreelanceOS.ExpanseTracking.DTO.ProjectExpenseRequest;
import FreelanceOS.ExpanseTracking.DTO.ProjectExpenseResponse;
import FreelanceOS.ExpanseTracking.Entity.ProjectExpense;
import FreelanceOS.ExpanseTracking.Service.ProjectExpenseService;
import FreelanceOS.Security.UserPrincipal;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Expense API", description = "Manage project expenses")
public class ProjectExpenseController {

    private final ProjectExpenseService service;

    public ProjectExpenseController(ProjectExpenseService service) {
        this.service = service;
    }

    //  CREATE
    @Operation(summary = "Create a new expense for a project")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Expense created",
                    content = @Content(schema = @Schema(implementation = ProjectExpense.class)))
    })
    @PostMapping("/{projectId}/expenses")
    public ResponseEntity<ProjectExpenseResponse> createExpense(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @RequestBody ProjectExpenseRequest request
    ) {
        return ResponseEntity.status(201)
                .body(service.createExpense(user.getId(), projectId, request));
    }

    //  GET
    @Operation(summary = "Get all expenses for a project with total sum")
    @GetMapping("/{projectId}/expenses")
    public ResponseEntity<ProjectExpenseListResponse> getProjectExpenses(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(
                service.getProjectExpenses(user.getId(), projectId)
        );
    }

    // UPDATE
    @Operation(summary = "Update an existing expense")
    @PutMapping("/{projectId}/expenses/{expenseId}")
    public ResponseEntity<ProjectExpenseResponse> updateExpense(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @PathVariable UUID expenseId,
            @Valid @RequestBody ProjectExpenseRequest request
    ) {
        return ResponseEntity.ok(
                service.updateExpense(user.getId(), projectId, expenseId, request)
        );
    }

    //  DELETE
    @Operation(summary = "Delete an expense")
    @DeleteMapping("/{projectId}/expenses/{expenseId}")
    public ResponseEntity<?> deleteExpense(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @PathVariable UUID expenseId
    ) {
        service.deleteExpense(user.getId(), projectId, expenseId);
        return ResponseEntity.noContent().build();
    }
}