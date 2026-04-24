package FreelanceOS.ClientManagement.Entity;


import FreelanceOS.ClientManagement.DTO.Address;
import FreelanceOS.ClientManagement.enums.AcquisitionSource;
import FreelanceOS.ClientManagement.enums.ClientStatus;
import FreelanceOS.ClientManagement.enums.ClientType;
import FreelanceOS.User.Entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "clients")
public class Client{

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "client_name", nullable = false)
    private String  clientName;

    private String companyName;

    private String email;

    private String phoneNumber;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private ClientType clientType;

    @Enumerated(EnumType.STRING)
    private AcquisitionSource acquisitionSource;

    private  String acquisitionDetails;

    @Enumerated(EnumType.STRING)
    private ClientStatus status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt=Instant.now();
        this.updatedAt=Instant.now();
    }
    @PreUpdate
    public void onUpdate(){
        this.updatedAt=Instant.now();
    }

}
