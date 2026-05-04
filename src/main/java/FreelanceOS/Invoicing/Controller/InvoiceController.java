package FreelanceOS.Invoicing.Controller;

import FreelanceOS.Invoicing.DTO.*;
import FreelanceOS.Invoicing.Enums.InvoiceStatus;
import FreelanceOS.Invoicing.Service.InvoiceService;
import FreelanceOS.Security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/invoices")
@Tag(name = "Invoice API", description = "Manage invoices")
@SecurityRequirement(name = "BearerAuth")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    // CREATE
    @Operation(summary = "Create a new invoice (Draft)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Invoice created",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))),
            @ApiResponse(responseCode = "409", description = "Invoice number conflict"),
            @ApiResponse(responseCode = "404", description = "Client or Project not found")
    })
    @PostMapping
    public ResponseEntity<InvoiceResponse> create(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody CreateInvoiceRequest req
    ) {
        return ResponseEntity.status(201)
                .body(service.create(user.getId(), req));
    }

    //  ADD LINE ITEM
    @Operation(summary = "Add line item to Draft invoice")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item added",
                    content = @Content(schema = @Schema(implementation = InvoiceLineItemResponse.class))),
            @ApiResponse(responseCode = "422", description = "Invoice is not Draft")
    })
    @PostMapping("/{id}/line-items")
    public ResponseEntity<InvoiceLineItemResponse> addItem(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @RequestBody InvoiceLineItemRequest req
    ) {
        return ResponseEntity.status(201)
                .body(service.addItem(user.getId(), id, req));
    }

    //  UPDATE LINE ITEM
    @Operation(summary = "Update line item (Draft only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item updated",
                    content = @Content(schema = @Schema(implementation = InvoiceLineItemResponse.class))),
            @ApiResponse(responseCode = "422", description = "Invoice is not Draft"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @PutMapping("/{id}/line-items/{itemId}")
    public ResponseEntity<InvoiceLineItemResponse> updateItem(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @PathVariable UUID itemId,
            @RequestBody InvoiceLineItemRequest req
    ) {
        return ResponseEntity.ok(
                service.updateLineItem(user.getId(), id, itemId, req)
        );
    }

    // DELETE LINE ITEM
    @Operation(summary = "Delete line item (Draft only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item deleted"),
            @ApiResponse(responseCode = "422", description = "Invoice is not Draft"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @DeleteMapping("/{id}/line-items/{itemId}")
    public ResponseEntity<?> deleteItem(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @PathVariable UUID itemId
    ) {
        service.deleteLineItem(user.getId(), id, itemId);
        return ResponseEntity.noContent().build();
    }

    // GET INVOICE
    @Operation(summary = "Get full invoice with totals")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice fetched",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> get(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(service.get(user.getId(), id));
    }

    //UPDATE STATUS
    @Operation(summary = "Update invoice status with validation rules")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status updated"),
            @ApiResponse(responseCode = "422", description = "Invalid status transition")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @RequestParam InvoiceStatus status
    ) {
        service.updateStatus(user.getId(), id, status);
        return ResponseEntity.ok().build();
    }

    //  ADD PAYMENT
    @Operation(summary = "Add payment to invoice")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment recorded",
                    content = @Content(schema = @Schema(implementation = InvoicePaymentResponse.class))),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    @PostMapping("/{id}/payments")
    public ResponseEntity<InvoicePaymentResponse> addPayment(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @RequestBody InvoicePaymentRequest req
    ) {
        return ResponseEntity.status(201)
                .body(service.addPayment(user.getId(), id, req));
    }

    // DELETE PAYMENT
    @Operation(summary = "Delete a payment")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Payment deleted"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @DeleteMapping("/{id}/payments/{payId}")
    public ResponseEntity<?> deletePayment(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id,
            @PathVariable UUID payId
    ) {
        service.deletePayment(user.getId(), id, payId);
        return ResponseEntity.noContent().build();
    }

    //  LIST
    @Operation(summary = "List invoices with filters and sorting")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoices fetched",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAll(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) UUID clientId,
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) String sortBy
    ) {
        return ResponseEntity.ok(
                service.getAll(user.getId(), status, clientId, projectId, sortBy)
        );
    }

    //OVERDUE
    @Operation(summary = "Get overdue invoices")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Overdue invoices fetched",
                    content = @Content(schema = @Schema(implementation = InvoiceResponse.class)))
    })
    @GetMapping("/overdue")
    public ResponseEntity<List<InvoiceResponse>> overdue(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return ResponseEntity.ok(service.getOverdue(user.getId()));
    }

    //  DELETE
    @Operation(summary = "Delete invoice (Draft or Cancelled only)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Invoice deleted"),
            @ApiResponse(responseCode = "422", description = "Cannot delete invoice with payments"),
            @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id
    ) {
        service.deleteInvoice(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}