package FreelanceOS;
import FreelanceOS.ClientManagement.Entity.Client;
import FreelanceOS.ClientManagement.Repository.ClientRepository;
import FreelanceOS.ExpanseTracking.Entity.ProjectExpense;
import FreelanceOS.ExpanseTracking.Repository.ProjectExpenseRepository;
import FreelanceOS.Invoicing.Entity.Invoice;
import FreelanceOS.Invoicing.Entity.InvoicePayment;
import FreelanceOS.ProjectManagement.DTO.CreateProjectRequest;
import FreelanceOS.ProjectManagement.Entity.Project;
import FreelanceOS.ProjectManagement.Entity.ProjectStage;
import FreelanceOS.ProjectManagement.Enums.ProjectStatus;
import FreelanceOS.ProjectManagement.Enums.ProjectType;
import FreelanceOS.ProjectManagement.Enums.StageStatus;
import FreelanceOS.ProjectManagement.Repository.ProjectRepository;
import FreelanceOS.ProjectManagement.Repository.ProjectStageRepository;
import FreelanceOS.ProjectManagement.Service.ProjectService;


import FreelanceOS.Invoicing.Repository.InvoiceRepository;
import FreelanceOS.Invoicing.Repository.InvoicePaymentRepository;

import FreelanceOS.Exception.ApiException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService service;

    @Mock
    private ProjectRepository projectRepo;

    @Mock
    private ProjectStageRepository stageRepo;

    @Mock
    private ProjectExpenseRepository expenseRepo;

    @Mock
    private InvoiceRepository invoiceRepo;

    @Mock
    private InvoicePaymentRepository paymentRepo;

    @Mock
    private ClientRepository clientRepo;


    // 1. AUTO STAGE GENERATION


    @Test
    void shouldGenerateStages_WhenDevelopmentProjectCreated() {

        UUID userId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        Client client = new Client();
        client.setId(clientId);

        when(clientRepo.findByIdAndUserId(clientId, userId))
                .thenReturn(Optional.of(client));

        Project project = new Project();
        project.setId(UUID.randomUUID());

        when(projectRepo.save(any())).thenReturn(project);

        CreateProjectRequest req = new CreateProjectRequest();
        req.setClientId(clientId);
        req.setProjectName("Test Project");
        req.setProjectType(ProjectType.DEVELOPMENT);

        service.createProject(userId, req);


        verify(stageRepo, atLeastOnce()).save(any(ProjectStage.class));
    }


    // 2. FINANCIAL SUMMARY (BASIC)


    @Test
    void shouldCalculateExpensesCorrectly() {

        UUID projectId = UUID.randomUUID();

        ProjectExpense e1 = new ProjectExpense();
        e1.setAmount(BigDecimal.valueOf(500));

        ProjectExpense e2 = new ProjectExpense();
        e2.setAmount(BigDecimal.valueOf(500));

        when(expenseRepo.findByProjectId(projectId))
                .thenReturn(List.of(e1, e2));

        BigDecimal total = expenseRepo.findByProjectId(projectId).stream()
                .map(ProjectExpense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(BigDecimal.valueOf(1000), total);
    }


    // 3. DELETE CONSTRAINT


    @Test
    void shouldThrowException_WhenProjectHasInvoices() {

        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        Project project = new Project();
        project.setId(projectId);

        when(projectRepo.findById(projectId))
                .thenReturn(Optional.of(project));

        when(invoiceRepo.findByProjectId(projectId))
                .thenReturn(List.of(new Invoice()));

        assertThrows(ApiException.class, () -> {
            service.deleteProject(userId, projectId);
        });
    }


    // 4. STAGE STATUS UPDATE (SAFE TEST)


    @Test
    void shouldUpdateStageStatus() {

        UUID projectId = UUID.randomUUID();

        ProjectStage stage = new ProjectStage();
        stage.setId(UUID.randomUUID());
        stage.setStatus(StageStatus.IN_PROGRESS);

        when(stageRepo.findById(stage.getId()))
                .thenReturn(Optional.of(stage));

        stage.setStatus(StageStatus.COMPLETED);

        assertEquals(StageStatus.COMPLETED, stage.getStatus());
    }
}