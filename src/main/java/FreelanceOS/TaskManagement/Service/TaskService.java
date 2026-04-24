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
import FreelanceOS.SecurityConfig.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectStageRepository stageRepository;
    private final JwtUtil jwtUtil;

    public TaskService(TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       ProjectStageRepository stageRepository,
                       JwtUtil jwtUtil) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.stageRepository = stageRepository;
        this.jwtUtil = jwtUtil;
    }


    // CREATE TASK

    public TaskResponse createTask(String token, UUID projectId, CreateTaskRequest request){

        UUID userId = jwtUtil.extractUserId(token);

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        ProjectStage stage = stageRepository
                .findById(request.getStageId())
                .orElseThrow(() -> new RuntimeException("Stage not found"));

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


    // GET TASKS

    public List<TaskResponse> getTasks(
            String token,
            UUID projectId,
            UUID stageId,
            TaskStatus status){

        UUID userId = jwtUtil.extractUserId(token);

        projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

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


    // UPDATE TASK

    public TaskResponse updateTask(
            String token,
            UUID projectId,
            UUID taskId,
            UpdateTaskRequest request){

        UUID userId = jwtUtil.extractUserId(token);

        ProjectTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getProject().getClient().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        task.setTaskTitle(request.getTaskTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        // 🔥 IMPORTANT LOGIC
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());

            if (request.getStatus() == TaskStatus.COMPLETED) {
                task.setCompletionDate(LocalDate.now());
            }
        }

        task.setUpdatedAt(LocalDateTime.now());

        return map(taskRepository.save(task));
    }


    // DELETE TASK

    public void deleteTask(String token, UUID projectId, UUID taskId){

        UUID userId = jwtUtil.extractUserId(token);

        ProjectTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getProject().getClient().getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        taskRepository.delete(task);
    }


    // MAPPER

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
