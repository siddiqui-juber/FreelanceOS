package FreelanceOS.ProjectManagement.Controller;

import FreelanceOS.ProjectManagement.DTO.*;
import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import FreelanceOS.ProjectManagement.Service.ProjectService;
import FreelanceOS.Security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Project API", description = "Manage projects and project stages")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    //  CREATE
    @Operation(summary = "Create a new project")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Project created",
                    content = @Content(schema = @Schema(implementation = ProjectResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody CreateProjectRequest request
    ){
        return ResponseEntity.status(201)
                .body(projectService.createProject(user.getId(), request));
    }

    //  GET ALL
    @Operation(summary = "Get all projects with filtering and sorting")
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) String sortBy
    ){
        return ResponseEntity.ok(
                projectService.getAllProjects(user.getId(), status, clientId, sortBy)
        );
    }

    // GET BY ID
    @Operation(summary = "Get project by ID with full details")
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId
    ){
        return ResponseEntity.ok(
                projectService.getProjectById(user.getId(), projectId)
        );
    }

    //UPDATE
    @Operation(summary = "Update project")
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateProjectRequest request
    ){
        return ResponseEntity.ok(
                projectService.updateProject(user.getId(), projectId, request)
        );
    }

    //  DELETE
    @Operation(summary = "Delete project")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId
    ){
        projectService.deleteProject(user.getId(), projectId);
        return ResponseEntity.ok(Map.of(
                "message", "Client Communication deleted successfully"
        ));
    }

    // ADD STAGE
    @Operation(summary = "Add stage to project")
    @PostMapping("/{projectId}/stages")
    public ResponseEntity<ProjectStageResponse> addStage(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @RequestParam String stageName
    ){
        return ResponseEntity.status(201)
                .body(projectService.addStage(user.getId(), projectId, stageName));
    }

    //  UPDATE STAGE
    @Operation(summary = "Update project stage")
    @PutMapping("/{projectId}/stages/{stageId}")
    public ResponseEntity<ProjectStageResponse> updateStage(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @PathVariable UUID stageId,
            @RequestBody UpdateStageRequest request
    ){
        return ResponseEntity.ok(
                projectService.updateStage(user.getId(), projectId, stageId, request)
        );
    }

    //  REORDER
    @Operation(summary = "Reorder stages")
    @PatchMapping("/{projectId}/stages/reorder")
    public ResponseEntity<?> reorder(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @RequestBody ReorderStagesRequest request
    ){
        projectService.reorderStages(user.getId(), projectId, request.getStageIds());
        return ResponseEntity.ok().build();
    }

    // ADVANCE
    @Operation(summary = "Advance to next stage")
    @PostMapping("/{projectId}/stages/advance")
    public ResponseEntity<?> advance(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId
    ){
        projectService.advanceStage(user.getId(), projectId);
        return ResponseEntity.ok().build();
    }
}