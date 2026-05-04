package FreelanceOS.ProjectDeliverable.Controller;

import FreelanceOS.ClientManagement.DTO.UpdateDeliverableRequest;
import FreelanceOS.ProjectDeliverable.DTO.DeliverableRequest;
import FreelanceOS.ProjectDeliverable.DTO.ProjectDeliverableResponse;
import FreelanceOS.ProjectDeliverable.Service.DeliverableService;
import FreelanceOS.Security.UserPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/projects")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Deliverable API", description = "Manage project deliverables")
public class DeliverableController {


    private final DeliverableService service;

    public DeliverableController(DeliverableService service) {
        this.service = service;
    }

    // CREATE
    @Operation(summary = "Create a deliverable")
    @PostMapping("/{projectId}/deliverables")
    public ResponseEntity<ProjectDeliverableResponse> createDeliverable(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @RequestBody DeliverableRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createDeliverable(user.getId(), projectId, request));
    }

    //  GET
    @Operation(summary = "Get all deliverables for a project")
    @GetMapping("/{projectId}/deliverables")
    public ResponseEntity<List<ProjectDeliverableResponse>> getProjectDeliverables(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId
    ) {
        return ResponseEntity.ok(
                service.getProjectDeliverables(user.getId(), projectId)
        );
    }

    //  UPDATE
    @Operation(summary = "Update deliverable status and feedback")
    @PutMapping("/{projectId}/deliverables/{deliverableId}")
    public ResponseEntity<ProjectDeliverableResponse> updateDeliverable(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @PathVariable UUID deliverableId,
            @RequestBody UpdateDeliverableRequest request  // ✔ fix here too
    ){
        return ResponseEntity.ok(
                service.updateDeliverable(user.getId(), projectId, deliverableId, request)
        );
    }

    // DELETE
    @Operation(summary = "Delete a deliverable")
    @DeleteMapping("/{projectId}/deliverables/{deliverableId}")
    public ResponseEntity<?> deleteDeliverable(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @PathVariable UUID deliverableId
    ) {
        service.deleteDeliverable(user.getId(), projectId, deliverableId);
        return ResponseEntity.noContent().build();
    }
}