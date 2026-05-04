package FreelanceOS.Security;




import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class UserPrincipal implements UserDetails {

    private UUID id;

    public UserPrincipal(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public Collection getAuthorities() {
        return Collections.emptyList();
    }

    @Override public String getPassword() { return null; }
    @Override public String getUsername() { return null; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
