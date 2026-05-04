package FreelanceOS.TaskManagement.Service;

import FreelanceOS.TaskManagement.DTO.TaskResponse;
import FreelanceOS.TaskManagement.DTO.CreateTaskRequest;
import FreelanceOS.TaskManagement.DTO.UpdateTaskRequest;
import FreelanceOS.ProjectManagement.Entity.Project;
import FreelanceOS.ProjectManagement.Entity.ProjectStage;
import FreelanceOS.TaskManagement.Entity.ProjectTask;
import FreelanceOS.TaskManagement.Enums.TaskStatus;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import FreelanceOS.ProjectManagement.Repository.ProjectStageRepository;
import FreelanceOS.TaskManagement.Repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectStageRepository stageRepository;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       ProjectStageRepository stageRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.stageRepository = stageRepository;
    }

    // CREATE
    public TaskResponse createTask(UUID userId, UUID projectId, CreateTaskRequest request){

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        ProjectStage stage = stageRepository
                .findById(request.getStageId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stage not found"
                ));

        // IMPORTANT: stage must belong to project
        if (!stage.getProject().getId().equals(projectId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Stage does not belong to this project"
            );
        }

        ProjectTask task = new ProjectTask();
        task.setProject(project);
        task.setStage(stage);
        task.setTaskTitle(request.getTaskTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setStatus(TaskStatus.PENDING);
        task.setCreatedAt(LocalDateTime.now());

        return map(taskRepository.save(task));
    }

    //  GET
    public List<TaskResponse> getTasks(
            UUID userId,
            UUID projectId,
            UUID stageId,
            TaskStatus status){

        projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        List<ProjectTask> tasks;

        if (stageId != null) {
            tasks = taskRepository.findByProjectIdAndStageId(projectId, stageId);
        } else if (status != null) {
            tasks = taskRepository.findByProjectIdAndStatus(projectId, status);
        } else {
            tasks = taskRepository.findByProjectId(projectId);
        }

        return tasks.stream().map(this::map).toList();
    }

    //  UPDATE
    public TaskResponse updateTask(
            UUID userId,
            UUID projectId,
            UUID taskId,
            UpdateTaskRequest request){

        ProjectTask task = taskRepository
                .findByIdAndProject_Client_UserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"
                ));

        task.setTaskTitle(request.getTaskTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());

            if (request.getStatus() == TaskStatus.COMPLETED) {
                task.setCompletionDate(LocalDate.now());
            }
        }

        task.setUpdatedAt(LocalDateTime.now());

        return map(taskRepository.save(task));
    }

    //  DELETE
    public void deleteTask(UUID userId, UUID projectId, UUID taskId){

        ProjectTask task = taskRepository
                .findByIdAndProject_Client_UserId(taskId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"
                ));

        taskRepository.delete(task);
    }

    //  MAPPER
    private TaskResponse map(ProjectTask task){

        TaskResponse r = new TaskResponse();

        r.setId(task.getId());
        r.setStageId(task.getStage().getId());
        r.setTaskTitle(task.getTaskTitle());
        r.setDescription(task.getDescription());
        r.setStatus(task.getStatus());
        r.setPriority(task.getPriority());
        r.setDueDate(task.getDueDate());
        r.setCompletionDate(task.getCompletionDate());

        return r;
    }
}