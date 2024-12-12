package kg.alatoo.entryapi.entities.tokens;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import kg.alatoo.entryapi.entities.User;
import kg.alatoo.entryapi.enums.TokenType;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24; // Token validity: 24 hours

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private Date expiryDate;

    public VerificationToken() {
    }

    public VerificationToken(String token, User user, TokenType tokenType) {
        this.token = token;
        this.user = user;
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}

