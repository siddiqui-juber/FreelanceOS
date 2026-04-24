package FreelanceOS.ProjectManagement.DTO;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ReorderStagesRequest {
    private List<UUID> stageIds;
}
