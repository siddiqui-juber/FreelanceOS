package FreelanceOS.ClientManagement.Controller;

import FreelanceOS.ClientManagement.DTO.ClientResponse;
import FreelanceOS.ClientManagement.DTO.CreateClientRequest;
import FreelanceOS.ClientManagement.Service.ClientService;
import FreelanceOS.ClientManagement.enums.ClientStatus;
import FreelanceOS.ClientManagement.enums.ClientType;
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
@RequestMapping("/api/v1/clients")
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "Client API", description = "Manage clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    //  CREATE
    @Operation(summary = "Create a new client")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client created",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody CreateClientRequest request
    ){
        return ResponseEntity.status(201)
                .body(clientService.createClient(user.getId(), request));
    }

    //  GET ALL
    @Operation(summary = "Get clients with filtering and sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getClients(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) ClientStatus status,
            @RequestParam(required = false) ClientType clientType,
            @RequestParam(required = false) String sortBy
    ){
        return ResponseEntity.ok(
                clientService.getClients(user.getId(), status, clientType, sortBy)
        );
    }

    // GET BY ID
    @Operation(summary = "Get client by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{clientId}")
    public ResponseEntity<ClientResponse> getClient(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID clientId
    ){
        return ResponseEntity.ok(
                clientService.getClientById(user.getId(), clientId)
        );
    }

    // UPDATE
    @Operation(summary = "Update client by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PutMapping("/{clientId}")
    public ResponseEntity<ClientResponse> updateClient(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID clientId,
            @RequestBody CreateClientRequest request
    ){
        return ResponseEntity.ok(
                clientService.updateClient(user.getId(), clientId, request)
        );
    }

    // DELETE
    @Operation(summary = "Delete client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "422", description = "Client has active projects")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id
    ){
        clientService.deleteClient(user.getId(), id);

        return ResponseEntity.ok(Map.of(
                "message", "Client deleted successfully"
        ));
    }
}