package FreelanceOS.ProjectDeliverable.Service;

import FreelanceOS.ClientManagement.DTO.UpdateDeliverableRequest;
import FreelanceOS.Exception.ApiException;
import FreelanceOS.ProjectDeliverable.DTO.DeliverableRequest;
import FreelanceOS.ProjectDeliverable.DTO.ProjectDeliverableResponse;
import FreelanceOS.ProjectDeliverable.Entity.ProjectDeliverable;
import FreelanceOS.ProjectDeliverable.Enums.DeliverableStatus;
import FreelanceOS.ProjectDeliverable.Repository.DeliverableRepository;
import FreelanceOS.ProjectManagement.Entity.Project;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DeliverableService {

    private final DeliverableRepository deliverableRepository;
    private final ProjectRepository projectRepository;

    public DeliverableService(DeliverableRepository deliverableRepository,
                              ProjectRepository projectRepository) {
        this.deliverableRepository = deliverableRepository;
        this.projectRepository = projectRepository;
    }

   //CREATE
    public ProjectDeliverableResponse createDeliverable(UUID userId,
                                                        UUID projectId,
                                                        DeliverableRequest request) {


        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> ApiException.notFound("Project not found"));

        ProjectDeliverable deliverable = new ProjectDeliverable();

        deliverable.setProject(project);

        deliverable.setDeliverableTitle(request.getDeliverableTitle());
        deliverable.setDescription(request.getDescription());
        deliverable.setDueDate(request.getDueDate());
        deliverable.setClientFeedback(request.getClientFeedback());

        DeliverableStatus status = request.getStatus() != null
                ? request.getStatus()
                : DeliverableStatus.NOT_STARTED;

        deliverable.setStatus(status);

        if (status == DeliverableStatus.DELIVERED) {
            deliverable.setDeliveryDate(
                    request.getDeliveryDate() != null
                            ? request.getDeliveryDate()
                            : LocalDate.now()
            );
        } else {
            deliverable.setDeliveryDate(null);
        }

        deliverable.setCreatedAt(LocalDateTime.now());
        deliverable.setUpdatedAt(LocalDateTime.now());

        ProjectDeliverable saved = deliverableRepository.save(deliverable);

        return mapToResponse(saved);
    }

    // GET
    public List<ProjectDeliverableResponse> getProjectDeliverables(UUID userId, UUID projectId) {

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        return deliverableRepository
                .findByProjectIdOrderByCreatedAtDesc(project.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

    }

    // UPDATE
    public ProjectDeliverableResponse updateDeliverable(UUID userId,
                                                        UUID projectId,
                                                        UUID deliverableId,
                                                        UpdateDeliverableRequest request) {

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        ProjectDeliverable deliverable = deliverableRepository
                .findByIdAndProjectId(deliverableId, project.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Deliverable not found"
                ));

        if (request.getStatus() != null) {
            deliverable.setStatus(request.getStatus());
            deliverable.setDeliveryDate(resolveUpdatedDeliveryDate(
                    request.getStatus(),
                    request.getDeliveryDate(),
                    deliverable.getDeliveryDate()
            ));
        } else if (request.getDeliveryDate() != null) {
            deliverable.setDeliveryDate(request.getDeliveryDate());
        }

        deliverable.setClientFeedback(request.getClientFeedback());

        return mapToResponse(deliverableRepository.save(deliverable));
    }

    //  DELETE
    public void deleteDeliverable(UUID userId, UUID projectId, UUID deliverableId) {

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        ProjectDeliverable deliverable = deliverableRepository
                .findByIdAndProjectId(deliverableId, project.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Deliverable not found"
                ));

        deliverableRepository.delete(deliverable);
    }

    //  HELPER
    private LocalDate resolveUpdatedDeliveryDate(DeliverableStatus status,
                                                 LocalDate requestDate,
                                                 LocalDate existingDate) {

        if (status == DeliverableStatus.DELIVERED) {
            if (requestDate != null) return requestDate;
            if (existingDate != null) return existingDate;
            return LocalDate.now();
        }
        return requestDate != null ? requestDate : existingDate;
    }

    //  MAPPER
    private ProjectDeliverableResponse mapToResponse(ProjectDeliverable d) {

        ProjectDeliverableResponse r = new ProjectDeliverableResponse();

        r.setId(d.getId());
        r.setProjectId(d.getProject().getId());


        r.setDeliverableTitle(d.getDeliverableTitle());
        r.setDescription(d.getDescription());
        r.setStatus(d.getStatus());
        r.setDueDate(d.getDueDate());
        r.setDeliveryDate(d.getDeliveryDate());
        r.setClientFeedback(d.getClientFeedback());
        r.setCreatedAt(d.getCreatedAt());
        r.setUpdatedAt(d.getUpdatedAt());

        return r;
    }
}