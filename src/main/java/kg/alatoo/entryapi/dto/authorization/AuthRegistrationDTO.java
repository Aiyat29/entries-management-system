package kg.alatoo.entryapi.dto.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthRegistrationDTO {
    private String username;
    private String email;
    private String password;
}
