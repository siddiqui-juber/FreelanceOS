package FreelanceOS.TaskManagement.Controller;


import FreelanceOS.TaskManagement.DTO.CreateTaskRequest;
import FreelanceOS.TaskManagement.DTO.TaskResponse;
import FreelanceOS.TaskManagement.DTO.UpdateTaskRequest;
import FreelanceOS.TaskManagement.Enums.TaskStatus;
import FreelanceOS.TaskManagement.Service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{id}/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private String extract(String header){
        return header.substring(7);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id,
            @RequestBody CreateTaskRequest request
    ){
        return ResponseEntity.status(201)
                .body(taskService.createTask(extract(header), id, request));
    }

    // GET
    @GetMapping
    public ResponseEntity<List<TaskResponse>> get(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id,
            @RequestParam(required = false) UUID stageId,
            @RequestParam(required = false) TaskStatus status
    ){
        return ResponseEntity.ok(
                taskService.getTasks(extract(header), id, stageId, status)
        );
    }

    // UPDATE
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> update(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id,
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskRequest request
    ){
        return ResponseEntity.ok(
                taskService.updateTask(extract(header), id, taskId, request)
        );
    }

    // DELETE
    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> delete(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id,
            @PathVariable UUID taskId
    ){
        taskService.deleteTask(extract(header), id, taskId);
        return ResponseEntity.noContent().build();
    }
}