package kg.alatoo.entryapi.repositories;


import kg.alatoo.entryapi.entities.tokens.VerificationToken;
import kg.alatoo.entryapi.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByTokenAndTokenType(String token, TokenType tokenType);
}

