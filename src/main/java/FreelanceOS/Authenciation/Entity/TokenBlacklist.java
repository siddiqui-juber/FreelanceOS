package FreelanceOS.Authenciation.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "token_blacklist")
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "token" ,nullable = false,unique = true,length = 500)
    private String token;

    @Column(name = "blacklisted_at")
    private Instant blacklistedAt;

    @PrePersist
    public void onCreate(){
        this.blacklistedAt = Instant.now();
    }
}
