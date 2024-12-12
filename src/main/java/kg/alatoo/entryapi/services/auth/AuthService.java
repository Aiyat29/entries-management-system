package kg.alatoo.entryapi.services.auth;


import kg.alatoo.entryapi.dto.UserDTO;
import kg.alatoo.entryapi.dto.authorization.AuthRegistrationDTO;
import kg.alatoo.entryapi.dto.reset.PasswordResetDTO;

public interface AuthService {
    UserDTO register(AuthRegistrationDTO authRegistrationDTO);

    String verifyEmail(String token);

    void sendPasswordResetRequest(PasswordResetDTO passwordResetDTO);

    String resetPassword(String token, String newPassword);
}
