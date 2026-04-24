package FreelanceOS.ProjectManagement.Controller;

import FreelanceOS.ProjectManagement.DTO.*;
import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import FreelanceOS.ProjectManagement.Service.ProjectService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;




@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    private String extractToken(String header){
        return header.replace("Bearer ", "");
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @RequestHeader("Authorization") String header,
            @Valid @RequestBody CreateProjectRequest request
    ){
        String token = header.replace("Bearer ", "");

        return ResponseEntity.status(201)
                .body(projectService.createProject(token, request));
    }


    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(
            @RequestHeader("Authorization") String header,
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) String sortBy
    ){
        String token = extractToken(header);

        return ResponseEntity.ok(
                projectService.getProjects(token, status, clientId, sortBy)
        );
    }


    //  GET PROJECT BY ID

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDetailResponse> getProjectById(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id
    ){
        String token = extractToken(header);

        return ResponseEntity.ok(
                projectService.getProjectById(token, id)
        );
    }


    //  UPDATE PROJECT

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id,
            @Valid @RequestBody CreateProjectRequest request
    ){
        String token = extractToken(header);

        return ResponseEntity.ok(
                projectService.updateProject(token, id, request)
        );
    }


    // DELETE PROJECT

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id
    ){
        String token = extractToken(header);

        projectService.deleteProject(token, id);

        return ResponseEntity.noContent().build(); // 204
    }


    @PostMapping("/{id}/stages")
    public ResponseEntity<ProjectStageResponse> addStage(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id,
            @RequestParam String stageName
    ) {
        String token = header.substring(7);

        return ResponseEntity.status(201)
                .body(projectService.addStage(token, id, stageName));
    }

    @PutMapping("/{id}/stages/{stageId}")
    public ResponseEntity<ProjectStageResponse> updateStage(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id,
            @PathVariable UUID stageId,
            @RequestBody UpdateStageRequest request
    ){
        return ResponseEntity.ok(
                projectService.updateStage(
                        extractToken(header),
                        id,
                        stageId,
                        request
                )
        );
    }

    @PatchMapping("/{id}/stages/reorder")
    public ResponseEntity<?> reorder(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id,
            @RequestBody ReorderStagesRequest request
    ){
        String token = header.substring(7);

        projectService.reorderStages(token, id, request.getStageIds());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/stages/advance")
    public ResponseEntity<?> advance(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id
    ){
        String token = header.substring(7);

        projectService.advanceStage(token, id);

        return ResponseEntity.ok().build();
    }



}
