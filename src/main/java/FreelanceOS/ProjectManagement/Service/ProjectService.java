package FreelanceOS.ProjectManagement.Service;

import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Repository.ClientRepository;
import FreelanceOS.ProjectManagement.DTO.*;
import FreelanceOS.ProjectManagement.Entity.Project;
import FreelanceOS.ProjectManagement.Entity.ProjectStage;
import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import FreelanceOS.ProjectManagement.Enums.ProjectType;
import FreelanceOS.ProjectManagement.Enums.StageStatus;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import FreelanceOS.ProjectManagement.Repository.ProjectStageRepository;
import FreelanceOS.SecurityConfig.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectStageRepository stageRepository;
    private final ClientRepository clientRepository;
    private final JwtUtil jwtUtil;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectStageRepository stageRepository,
                          ClientRepository clientRepository,
                          JwtUtil jwtUtil) {
        this.projectRepository = projectRepository;
        this.stageRepository = stageRepository;
        this.clientRepository = clientRepository;
        this.jwtUtil = jwtUtil;
    }
    public ProjectResponse createProject(String token, CreateProjectRequest request){

        UUID userId = jwtUtil.extractUserId(token);

        Client client = clientRepository
                .findByIdAndUserId(request.getClientId(), userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Project project = new Project();
        project.setClient(client);
        project.setProjectName(request.getProjectName());
        project.setProjectType(request.getProjectType());
        project.setStatus(ProjectStatus.ACTIVE);
        project.setStartDate(request.getStartDate());
        project.setCreatedAt(LocalDateTime.now());

        Project savedProject = projectRepository.save(project);

        List<ProjectStage> createdStages = new ArrayList<>();

        if (request.getProjectType() == ProjectType.DEVELOPMENT) {

            List<String> stages = getDevelopmentStages();

            int order = 1;

            for (String name : stages) {
                ProjectStage stage = new ProjectStage();

                stage.setProject(savedProject);
                stage.setStageName(name);
                stage.setStageOrder(order++);
                stage.setStatus(StageStatus.PENDING);

                createdStages.add(stageRepository.save(stage));
            }
        }

        return mapToResponse(savedProject, createdStages);
    }


    private List<String> getDevelopmentStages() {
        return List.of(
                "Requirement Gathering",
                "Design",
                "Development",
                "Testing",
                "Deployment",
                "Maintenance"
        );
    }


    private ProjectResponse mapToResponse(Project project, List<ProjectStage> stages){

        ProjectResponse response = new ProjectResponse();

        response.setId(project.getId());
        response.setClientId(project.getClient().getId());
        response.setProjectName(project.getProjectName());
        response.setProjectType(project.getProjectType());
        response.setStatus(project.getStatus());
        response.setStartDate(project.getStartDate());
        response.setCreatedAt(project.getCreatedAt());

        List<ProjectStageResponse> stageResponses = stages.stream().map(stage -> {

            ProjectStageResponse s = new ProjectStageResponse();

            s.setId(stage.getId());
            s.setStageName(stage.getStageName());
            s.setStageOrder(stage.getStageOrder());
            s.setStatus(stage.getStatus());
            s.setStartDate(stage.getStartDate());
            s.setCompletionDate(stage.getCompletionDate());

            return s;

        }).toList();

        response.setStages(stageResponses);

        return response;
    }

    // GET PROJECT BY ID
    public ProjectDetailResponse getProjectById(String token, UUID projectId){

        UUID userId = jwtUtil.extractUserId(token);

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<ProjectStage> stages =
                stageRepository.findByProjectIdOrderByStageOrder(projectId);

        ProjectDetailResponse res = new ProjectDetailResponse();

        res.setId(project.getId());
        res.setClientId(project.getClient().getId());
        res.setProjectName(project.getProjectName());
        res.setProjectType(project.getProjectType());
        res.setStatus(project.getStatus());
        res.setStartDate(project.getStartDate());
        res.setCreatedAt(project.getCreatedAt());

        // map stages
        List<ProjectStageResponse> stageResponses = stages.stream().map(s -> {
            ProjectStageResponse r = new ProjectStageResponse();
            r.setId(s.getId());
            r.setStageName(s.getStageName());
            r.setStageOrder(s.getStageOrder());
            r.setStatus(s.getStatus());
            return r;
        }).toList();

        res.setStages(stageResponses);

        //  TEMP VALUES
        res.setTotalTasks(0);
        res.setCompletedTasks(0);
        res.setTotalDeliverables(0);

        res.setBudget(0);
        res.setTotalExpenses(0);
        res.setTotalInvoiced(0);
        res.setTotalPaid(0);
        res.setOutstanding(0);
        res.setNetProfit(0);
        res.setProfitMargin(0);

        return res;
    }

    //GET ALL PROJECTS
    public List<ProjectResponse> getProjects(
            String token,
            ProjectStatus status,
            UUID clientId,
            String sortBy){

        UUID userId = jwtUtil.extractUserId(token);

        List<Project> projects =
                projectRepository.findByClient_UserId(userId);

        if (status != null) {
            projects = projects.stream()
                    .filter(p -> p.getStatus() == status)
                    .toList();
        }

        if (clientId != null) {
            projects = projects.stream()
                    .filter(p -> p.getClient().getId().equals(clientId))
                    .toList();
        }

        if ("created_at".equalsIgnoreCase(sortBy)) {
            projects.sort(Comparator.comparing(Project::getCreatedAt));
        }

        return projects.stream()
                .map(p -> {
                    ProjectResponse r = new ProjectResponse();
                    r.setId(p.getId());
                    r.setProjectName(p.getProjectName());
                    r.setStatus(p.getStatus());
                    return r;
                }).toList();
    }

    //UPDATE PROJECT
    public ProjectResponse updateProject(
            String token,
            UUID projectId,
            CreateProjectRequest request){

        UUID userId = jwtUtil.extractUserId(token);

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        project.setProjectName(request.getProjectName());
        project.setProjectType(request.getProjectType());
        project.setStartDate(request.getStartDate());

        Project updated = projectRepository.save(project);

        ProjectResponse res = new ProjectResponse();
        res.setId(updated.getId());
        res.setProjectName(updated.getProjectName());

        return res;
    }

    //DELETE PROJECTS
    public void deleteProject(String token, UUID projectId){

        UUID userId = jwtUtil.extractUserId(token);

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        boolean hasInvoices = false; // 🔥 future logic

        if (hasInvoices) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Project has invoices"
            );
        }

        projectRepository.delete(project);
    }

    public ProjectStageResponse addStage(String token, UUID projectId, String stageName){

        UUID userId = jwtUtil.extractUserId(token);

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        int nextOrder = (int) (stageRepository.countByProjectId(projectId) + 1);

        ProjectStage stage = new ProjectStage();
        stage.setProject(project);
        stage.setStageName(stageName);
        stage.setStageOrder(nextOrder);
        stage.setStatus(StageStatus.PENDING);

        ProjectStage saved = stageRepository.save(stage);

        return mapStage(saved);
    }

    public ProjectStageResponse updateStage(
            String token,
            UUID projectId,
            UUID stageId,
            UpdateStageRequest request){

        UUID userId = jwtUtil.extractUserId(token);

        ProjectStage stage = stageRepository
                .findByIdAndProject_Client_UserId(stageId, userId)
                .orElseThrow(() -> new RuntimeException("Stage not found"));

        stage.setStageName(request.getStageName());
        stage.setStatus(request.getStatus());
        stage.setStartDate(request.getStartDate());
        stage.setCompletionDate(request.getCompletionDate());

        return mapStage(stageRepository.save(stage));
    }

    public void reorderStages(String token, UUID projectId, List<UUID> stageIds){

        UUID userId = jwtUtil.extractUserId(token);

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<ProjectStage> stages = stageRepository.findByProjectId(projectId);

        Map<UUID, ProjectStage> map = stages.stream()
                .collect(Collectors.toMap(ProjectStage::getId, s -> s));

        int order = 1;

        for (UUID id : stageIds){
            ProjectStage stage = map.get(id);
            if (stage != null){
                stage.setStageOrder(order++);
            }
        }

        stageRepository.saveAll(stages);
    }


    public void advanceStage(String token, UUID projectId){

        UUID userId = jwtUtil.extractUserId(token);

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<ProjectStage> stages =
                stageRepository.findByProjectIdOrderByStageOrder(projectId);

        ProjectStage current = stages.stream()
                .filter(s -> s.getStatus() == StageStatus.IN_PROGRESS)
                .findFirst()
                .orElse(null);

        if (current == null){
            // first stage
            stages.get(0).setStatus(StageStatus.IN_PROGRESS);
            stageRepository.save(stages.get(0));
            return;
        }

        int index = stages.indexOf(current);

        // last stage
        if (index == stages.size() - 1){
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Already at last stage"
            );
        }

        // complete current
        current.setStatus(StageStatus.COMPLETED);
        current.setCompletionDate(LocalDate.now());

        // next stage
        ProjectStage next = stages.get(index + 1);
        next.setStatus(StageStatus.IN_PROGRESS);
        next.setStartDate(LocalDate.now());

        stageRepository.saveAll(List.of(current, next));
    }

    private ProjectStageResponse mapStage(ProjectStage stage){

        ProjectStageResponse r = new ProjectStageResponse();

        r.setId(stage.getId());
        r.setStageName(stage.getStageName());
        r.setStageOrder(stage.getStageOrder());
        r.setStatus(stage.getStatus());

        return r;
    }







    }

