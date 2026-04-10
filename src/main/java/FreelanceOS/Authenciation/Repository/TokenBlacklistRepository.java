package FreelanceOS.Authenciation.Repository;

import FreelanceOS.Authenciation.Entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    boolean existsByToken(String token);
}