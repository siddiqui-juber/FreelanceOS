package FreelanceOS.TaskManagement.Controller;


import FreelanceOS.Security.UserPrincipal;
import FreelanceOS.TaskManagement.DTO.CreateTaskRequest;
import FreelanceOS.TaskManagement.DTO.TaskResponse;
import FreelanceOS.TaskManagement.DTO.UpdateTaskRequest;
import FreelanceOS.TaskManagement.Enums.TaskStatus;
import FreelanceOS.TaskManagement.Service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/tasks")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Task API", description = "Manage tasks within a project")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    //  CREATE
    @Operation(summary = "Create a new task")
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @RequestBody CreateTaskRequest request
    ){
        return ResponseEntity.status(201)
                .body(taskService.createTask(user.getId(), projectId, request));
    }

    //  GET
    @Operation(summary = "Get tasks with optional filters")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> get(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @RequestParam(required = false) UUID stageId,
            @RequestParam(required = false) TaskStatus status
    ){
        return ResponseEntity.ok(
                taskService.getTasks(user.getId(), projectId, stageId, status)
        );
    }

    //  UPDATE
    @Operation(summary = "Update task details")
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> update(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskRequest request
    ){
        return ResponseEntity.ok(
                taskService.updateTask(user.getId(), projectId, taskId, request)
        );
    }

    //  DELETE
    @Operation(summary = "Delete a task")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId
    ){
        taskService.deleteTask(user.getId(), projectId, taskId);
        return ResponseEntity.noContent().build();
    }
}