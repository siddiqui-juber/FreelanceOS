package FreelanceOS.ClientManagement.Entity;

import FreelanceOS.ClientManagement.enums.CommunicationType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "client_Communication")
public class ClientCommunication {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    private CommunicationType communicationType;

    private String subjects;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Instant communicationDate;

    private Instant createdAt;

    @PrePersist
    public void onCreate(){
        this.createdAt=Instant.now();

        if (this.communicationDate==null){
            this.communicationDate=Instant.now();
        }
    }
}
