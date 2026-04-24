package FreelanceOS.ClientManagement.Controller;

import FreelanceOS.ClientManagement.DTO.ClientResponse;
import FreelanceOS.ClientManagement.DTO.CreateClientRequest;
import FreelanceOS.ClientManagement.Service.ClientService;
import FreelanceOS.ClientManagement.enums.ClientStatus;
import FreelanceOS.ClientManagement.enums.ClientType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService){
        this.clientService=clientService;
    }
    private String extractToken(String header){
        return header.replace("Bearer","").trim();
    }

    @Operation(summary = "Create a new client")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Client created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(
            @RequestHeader("Authorization") String header,
            @RequestBody CreateClientRequest request
    ){
        String token = extractToken(header);

        ClientResponse response = clientService.createClient(token, request);

        return ResponseEntity.status(201).body(response);
    }


    @Operation(summary = "Get client ")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getClients(
            @RequestHeader("Authorization")String header,
            @RequestParam(required = false)ClientStatus status,
            @RequestParam(required = false)ClientType clientType,
            @RequestParam(required = false)String sortBy
            ){
        String token = extractToken(header);
        return ResponseEntity.ok(
                clientService.getClients(token,status,clientType,sortBy)
        );
    }

    @Operation(summary = "Get client by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(schema = @Schema(implementation = ClientResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClient(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id
            ){
        String token = extractToken(header);

        return ResponseEntity.ok(clientService.getClientById(token,id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id,
            @RequestBody CreateClientRequest request
    ){
         String token = extractToken(header);
         return ResponseEntity.ok(clientService.updateClient(token,id,request));
    }
// Implement DELETE /api/v1/clients/{id} — delete a client. Block deletion and
// return 422 if the client has any projects in a non-cancelled or non-completed status.
// i have to implement when working on project modules.
@Operation(summary = "Delete client")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deleted"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "422", description = "Client has active projects")
})
@DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id
    ){
        String token=extractToken(header);
        clientService.deleteClient(token,id);
        return ResponseEntity.ok(Map.of("message","Client deleted successfully"));
    }





}
