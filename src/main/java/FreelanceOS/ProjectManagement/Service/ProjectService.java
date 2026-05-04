package FreelanceOS.ProjectManagement.Service;

import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Repository.ClientRepository;
import FreelanceOS.Exception.ApiException;
import FreelanceOS.ExpanseTracking.Entity.ProjectExpense;
import FreelanceOS.ExpanseTracking.Repository.ProjectExpenseRepository;
import FreelanceOS.Invoicing.Entity.Invoice;
import FreelanceOS.Invoicing.Entity.InvoiceLineItem;
import FreelanceOS.Invoicing.Entity.InvoicePayment;
import FreelanceOS.Invoicing.Repository.InvoiceRepository;
import FreelanceOS.ProjectDeliverable.Repository.DeliverableRepository;
import FreelanceOS.ProjectManagement.DTO.*;
import FreelanceOS.ProjectManagement.Entity.Project;
import FreelanceOS.ProjectManagement.Entity.ProjectStage;
import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import FreelanceOS.ProjectManagement.Enums.ProjectType;
import FreelanceOS.ProjectManagement.Enums.StageStatus;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import FreelanceOS.ProjectManagement.Repository.ProjectStageRepository;
import FreelanceOS.TaskManagement.Enums.TaskStatus;
import FreelanceOS.TaskManagement.Repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectStageRepository stageRepository;
    private final ClientRepository clientRepository;
    private final InvoiceRepository invoiceRepository;
    private final TaskRepository taskRepository;
    private final DeliverableRepository deliverableRepository;
    private final ProjectExpenseRepository expenseRepository;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectStageRepository stageRepository,
                          ClientRepository clientRepository,
                          InvoiceRepository invoiceRepository,
                          TaskRepository taskRepository,
                          DeliverableRepository deliverableRepository,
                          ProjectExpenseRepository expenseRepository) {
        this.projectRepository = projectRepository;
        this.stageRepository = stageRepository;
        this.clientRepository = clientRepository;
        this.invoiceRepository = invoiceRepository;
        this.taskRepository=taskRepository;
        this.deliverableRepository=deliverableRepository;
        this.expenseRepository=expenseRepository;
    }

    //  CREATE
    public ProjectResponse createProject(UUID userId, CreateProjectRequest request){

        Client client = clientRepository
                .findByIdAndUserId(request.getClientId(), userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Client not found"
                ));

        Project project = new Project();
        project.setClient(client);
        project.setProjectName(request.getProjectName());
        project.setProjectType(request.getProjectType());
        project.setStatus(ProjectStatus.ACTIVE);
        project.setStartDate(request.getStartDate());

        Project savedProject = projectRepository.save(project);

        List<ProjectStage> createdStages = new ArrayList<>();

        if (request.getProjectType() == ProjectType.DEVELOPMENT) {

            int order = 1;

            for (String name : getDevelopmentStages()) {
                ProjectStage stage = new ProjectStage();
                stage.setProject(savedProject);
                stage.setStageName(name);
                stage.setStageOrder(order);

                if (order == 1) {
                    stage.setStatus(StageStatus.IN_PROGRESS);
                    stage.setStartDate(LocalDate.now());
                } else {
                    stage.setStatus(StageStatus.PENDING);
                }

                createdStages.add(stageRepository.save(stage));
                order++;
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

    //  GET BY ID
    public ProjectDetailResponse getProjectById(UUID userId, UUID projectId){

        Project project = projectRepository
                .findProjectWithClientAndStages(projectId, userId)
                .orElseThrow(() -> ApiException.notFound("Project not found"));

        ProjectDetailResponse res = new ProjectDetailResponse();

        //  BASIC
        res.setId(project.getId());

        if (project.getClient() != null) {
            res.setClientId(project.getClient().getId());
        }

        res.setProjectName(project.getProjectName());
        res.setProjectType(project.getProjectType());
        res.setStatus(project.getStatus());
        res.setStartDate(project.getStartDate());
        res.setCreatedAt(project.getCreatedAt());

        res.setStages(
                project.getStages() != null
                        ? project.getStages().stream().map(this::mapStage).toList()
                        : List.of()
        );

        //  TASKS
        long totalTasks = taskRepository.countByProjectId(projectId);

        long completedTasks = taskRepository
                .countByProjectIdAndStatus(projectId, TaskStatus.COMPLETED);

        res.setTotalTasks((int) totalTasks);
        res.setCompletedTasks((int) completedTasks);

        //  DELIVERABLES
        long totalDeliverables = deliverableRepository.countByProjectId(projectId);
        res.setTotalDeliverables((int) totalDeliverables);

        //  EXPENSES
        List<ProjectExpense> expenses = expenseRepository.findByProjectId(projectId);

        BigDecimal totalExpenses = expenses.stream()
                .map(e -> e.getAmount() != null ? e.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));

        res.setTotalExpenses(totalExpenses.doubleValue());

        //  INVOICES
        List<Invoice> invoices = invoiceRepository.findByProjectId(projectId);

        BigDecimal totalInvoiced = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;

        for (Invoice inv : invoices) {

            List<InvoiceLineItem> items = inv.getItems();

            BigDecimal subtotal = items.stream()
                    .map(i -> i.getQuantity().multiply(i.getUnitRate()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal discount = inv.getDiscountAmount() != null ? inv.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal taxable = subtotal.subtract(discount);

            BigDecimal taxRate = inv.getTaxRate() != null ? inv.getTaxRate() : BigDecimal.ZERO;
            BigDecimal tax = taxable.multiply(taxRate);

            BigDecimal total = taxable.add(tax);

            totalInvoiced = totalInvoiced.add(total);

            List<InvoicePayment> payments = inv.getPayments();

            BigDecimal paid = payments.stream()
                    .map(InvoicePayment::getAmountPaid)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalPaid = totalPaid.add(paid);
        }

        //  OUTSTANDING
        BigDecimal outstanding = totalInvoiced.subtract(totalPaid);
        res.setOutstanding(outstanding.doubleValue());

        //  PROFIT
        BigDecimal profit = totalInvoiced.subtract(totalExpenses);
        res.setNetProfit(profit.doubleValue());

        //  PROFIT MARGIN
        if (totalInvoiced.compareTo(BigDecimal.ZERO) > 0) {
            double margin = profit
                    .divide(totalInvoiced, 2, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();

            res.setProfitMargin(margin);
        } else {
            res.setProfitMargin(0);
        }

        return res;
    }

    // GET ALL
    public List<ProjectResponse> getAllProjects(UUID userId,
                                                ProjectStatus status,
                                                UUID clientId,
                                                String sortBy) {

        List<Project> projects = projectRepository.findByClient_UserId(userId);

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

        if ("createdAt".equalsIgnoreCase(sortBy)) {
            projects.sort(Comparator.comparing(Project::getCreatedAt));
        }

        return projects.stream()
                .map(this::mapProject)
                .toList();
    }

    // UPDATE
    public ProjectResponse updateProject(UUID userId, UUID projectId, CreateProjectRequest request){

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        project.setProjectName(request.getProjectName());
        project.setProjectType(request.getProjectType());
        project.setStartDate(request.getStartDate());

        return mapProject(projectRepository.save(project));
    }

    //  DELETE
    public void deleteProject(UUID userId, UUID projectId){

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        if (!invoiceRepository.findByProjectId(projectId).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Cannot delete project with existing invoices"
            );
        }

        projectRepository.delete(project);
    }

    //  STAGE
    public ProjectStageResponse addStage(UUID userId, UUID projectId, String stageName){

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

        int nextOrder = (int) (stageRepository.countByProjectId(projectId) + 1);

        ProjectStage stage = new ProjectStage();
        stage.setProject(project);
        stage.setStageName(stageName);
        stage.setStageOrder(nextOrder);

        if (nextOrder == 1) {
            stage.setStatus(StageStatus.IN_PROGRESS);
            stage.setStartDate(LocalDate.now());
        } else {
            stage.setStatus(StageStatus.PENDING);
        }

        return mapStage(stageRepository.save(stage));
    }

    //UPDATE STAGE
    public ProjectStageResponse updateStage(UUID userId,
                                            UUID projectId,
                                            UUID stageId,
                                            UpdateStageRequest request){

        ProjectStage stage = stageRepository
                .findByIdAndProject_Client_UserId(stageId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Stage not found"
                ));

        stage.setStageName(request.getStageName());
        stage.setStatus(request.getStatus());
        stage.setStartDate(request.getStartDate());
        stage.setCompletionDate(request.getCompletionDate());

        return mapStage(stageRepository.save(stage));
    }
    //REORDER STAGE
    public void reorderStages(UUID userId, UUID projectId, List<UUID> stageIds){

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Project not found"
                ));

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
    // ADVANCE STAGE
    public void advanceStage(UUID userId, UUID projectId){

        Project project = projectRepository
                .findByIdAndClient_UserId(projectId, userId)
                .orElseThrow(() -> ApiException.notFound("Project not found"));

        List<ProjectStage> stages =
                stageRepository.findByProjectIdOrderByStageOrder(projectId);

        ProjectStage current = stages.stream()
                .filter(s -> s.getStatus() == StageStatus.IN_PROGRESS)
                .findFirst()
                .orElse(null);

        if (current == null){
            ProjectStage first = stages.get(0);
            first.setStatus(StageStatus.IN_PROGRESS);
            first.setStartDate(LocalDate.now());
            stageRepository.save(first);
            return;
        }

        int index = stages.indexOf(current);

        if (index == stages.size() - 1){
            throw ApiException.unprocessable("Already at last stage");
        }

        current.setStatus(StageStatus.COMPLETED);
        current.setCompletionDate(LocalDate.now());

        ProjectStage next = stages.get(index + 1);
        next.setStatus(StageStatus.IN_PROGRESS);
        next.setStartDate(LocalDate.now());

        stageRepository.saveAll(List.of(current, next));
    }

    //  MAPPERS
    private ProjectResponse mapProject(Project p) {

        ProjectResponse res = new ProjectResponse();

        res.setId(p.getId());

        if (p.getClient() != null) {
            res.setClientId(p.getClient().getId());
        }

        res.setProjectName(p.getProjectName());
        res.setProjectType(p.getProjectType());
        res.setStatus(p.getStatus());
        res.setStartDate(p.getStartDate());
        res.setCreatedAt(p.getCreatedAt());

        List<ProjectStage> stages = stageRepository
                .findByProjectIdOrderByStageOrder(p.getId());


        ProjectStage currentStage = stages.stream()
                .filter(s -> s.getStatus() == StageStatus.IN_PROGRESS)
                .findFirst()
                .orElse(null);


        if (currentStage == null) {
            currentStage = stages.stream()
                    .filter(s -> s.getStatus() == StageStatus.PENDING)
                    .findFirst()
                    .orElse(null);
        }

       
        if (currentStage != null) {
            res.setCurrentStageName(currentStage.getStageName());
            res.setCurrentStageStatus(currentStage.getStatus().name());
        } else {
            res.setCurrentStageName("Completed");
            res.setCurrentStageStatus("DONE");
        }

        return res;
    }

    private ProjectStageResponse mapStage(ProjectStage stage){
        ProjectStageResponse r = new ProjectStageResponse();
        r.setId(stage.getId());
        r.setStageName(stage.getStageName());
        r.setStartDate(stage.getStartDate());
        r.setCompletionDate(stage.getCompletionDate());
        r.setStageOrder(stage.getStageOrder());
        r.setStatus(stage.getStatus());
        return r;
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

       // response.setStages(stages.stream().map(this::mapStage).toList());

        return response;
    }
}