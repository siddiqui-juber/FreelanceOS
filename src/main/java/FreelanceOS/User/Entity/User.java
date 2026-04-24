package FreelanceOS.User.Entity;

import FreelanceOS.Authenciation.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")})
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "email", nullable = false,unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "firstName", nullable = false)
    private String firstName;

    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "default_currency")
    private String defaultCurrency;

    private String timeZone;
    @Enumerated(EnumType.STRING)

    @Column(name = "account_status")
    private AccountStatus accountStatus;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name ="last_login_at")
    private Instant last_login_at;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.accountStatus = AccountStatus.ACTIVE;
        this.emailVerified = false;
    }
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = Instant.now();
    }



}
