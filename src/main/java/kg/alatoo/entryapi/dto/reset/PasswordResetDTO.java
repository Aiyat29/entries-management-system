package kg.alatoo.entryapi.dto.reset;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetDTO {
    private String email;
    private String newPassword;
}