package FreelanceOS.ClientManagement.Controller;

import FreelanceOS.ClientManagement.DTO.CommunicationResponse;
import FreelanceOS.ClientManagement.DTO.CreateCommunicationRequest;
import FreelanceOS.ClientManagement.Service.ClientCommunicationService;
import FreelanceOS.Security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/clients/{clientId}/communications")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Client Communication API", description = "Manage client communications")
public class ClientCommunicationController {

    private final ClientCommunicationService communicationService;

    public ClientCommunicationController(ClientCommunicationService communicationService){
        this.communicationService = communicationService;
    }

    // CREATE
    @Operation(summary = "Add a communication entry to a client")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Communication created",
                    content = @Content(schema = @Schema(implementation = CommunicationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PostMapping
    public ResponseEntity<CommunicationResponse> addCommunication(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID clientId,
            @RequestBody CreateCommunicationRequest request
    ){
        return ResponseEntity.status(201).body(
                communicationService.addCommunication(user.getId(), clientId, request)
        );
    }

    //  GET
    @Operation(summary = "Get all communications for a client (newest first)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of communications"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping
    public ResponseEntity<List<CommunicationResponse>> getCommunication(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID clientId
    ){
        return ResponseEntity.ok(
                communicationService.getCommunication(user.getId(), clientId)
        );
    }

    //  DELETE
    @Operation(summary = "Delete a specific communication entry")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Communication deleted"),
            @ApiResponse(responseCode = "404", description = "Client or communication not found")
    })
    @DeleteMapping("/{commId}")
    public ResponseEntity<?> deleteCommunication(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID clientId,
            @PathVariable UUID commId
    ){
        communicationService.deleteCommunication(user.getId(), clientId, commId);
        return ResponseEntity.ok(Map.of(
                        "message", "Client Communication deleted successfully"
                ));
    }
}