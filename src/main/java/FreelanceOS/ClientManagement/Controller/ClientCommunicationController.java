package FreelanceOS.ClientManagement.Controller;


import FreelanceOS.ClientManagement.DTO.CommunicationResponse;
import FreelanceOS.ClientManagement.DTO.CreateCommunicationRequest;
import FreelanceOS.ClientManagement.Service.ClientCommunicationService;
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
@RequestMapping("/api/v1/clients/{id}/communications")
public class ClientCommunicationController {

    private final ClientCommunicationService communicationService;

    public ClientCommunicationController(ClientCommunicationService communicationService){
        this.communicationService= communicationService;
    }
    private String extractToken(String header){
        return header.replace("Bearer"," ").trim();
    }

    @Operation(summary = "Add a communication entry to a client")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Communication created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommunicationResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input or invalid communication type"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PostMapping
    public ResponseEntity<CommunicationResponse>addCommunication(
            @RequestHeader ("Authorization") String header,
            @PathVariable UUID id,
            @RequestBody CreateCommunicationRequest request
            ){
        String token = extractToken(header);

        return ResponseEntity.ok(communicationService.addCommunication(token,id,request));
    }

    @Operation(summary = "Get all communications for a client (newest first)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "List of communications",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommunicationResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping
    public ResponseEntity<List<CommunicationResponse>> getCommunication(
            @RequestHeader("Authorization") String header,
            @PathVariable UUID id
    ){
        String token = extractToken(header);

        return ResponseEntity.ok(communicationService.getCommunication(token,id));
    }

    @Operation(summary = "Delete a specific communication entry")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Communication deleted"),
            @ApiResponse(responseCode = "404", description = "Client or communication not found")
    })
    @DeleteMapping("/{commId}")
    public ResponseEntity<?> deleteCommunication(
            @RequestHeader("Authorization")String header,
            @PathVariable  UUID id,
            @PathVariable UUID commId){
        String token = header.replace("Bearer","").trim();
        communicationService.deleteCommunication(token,id,commId);
        return ResponseEntity.ok(Map.of("message", "Communication deleted successfully"));
    }

}
