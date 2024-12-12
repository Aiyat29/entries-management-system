package kg.alatoo.entryapi.services.auth;

import kg.alatoo.entryapi.dto.UserDTO;
import kg.alatoo.entryapi.dto.authorization.AuthRegistrationDTO;
import kg.alatoo.entryapi.dto.reset.PasswordResetDTO;
import kg.alatoo.entryapi.entities.Role;
import kg.alatoo.entryapi.entities.User;
import kg.alatoo.entryapi.entities.tokens.VerificationToken;
import kg.alatoo.entryapi.enums.TokenType;
import kg.alatoo.entryapi.exceptions.ApiException;
import kg.alatoo.entryapi.mappers.UserMapper;
import kg.alatoo.entryapi.repositories.RoleRepository;
import kg.alatoo.entryapi.repositories.UserRepository;
import kg.alatoo.entryapi.repositories.VerificationTokenRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, UserMapper userMapper, VerificationTokenRepository verificationTokenRepository, PasswordEncoder passwordEncoder, MailService mailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    @Override
    public UserDTO register(AuthRegistrationDTO authRegistrationDTO) {
        Role role = roleRepository.findByName("ROLE_USER");

        User user = userMapper.authRegistrationDtoToUserEntity(authRegistrationDTO);
        user.setRoles(Set.of(role));
        user.setPassword(passwordEncoder.encode(authRegistrationDTO.getPassword()));
        user.setEnabled(false);

        User savedUser = userRepository.save(user);

        // Generate verification token
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, savedUser, TokenType.VERIFICATION);
        verificationTokenRepository.save(verificationToken);

        // Send verification email
        String verificationUrl = "http://104.248.46.170:8080/api/v1/auth/verify-email?token=" + token;
        String subject = "Email Verification";
        String body = "Please click the following link to verify your email: " + verificationUrl;

        mailService.sendEmail(savedUser.getEmail(), subject, body);

        return userMapper.userToUserDto(savedUser);
    }

    @Override
    public String verifyEmail(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByTokenAndTokenType(token, TokenType.VERIFICATION);

        if (verificationToken == null) {
            throw new IllegalArgumentException("Invalid verification token.");
        }

        User user = verificationToken.getUser();
        if (user == null) {
            throw new IllegalArgumentException("No user found for this token.");
        }

        // Check if token is expired
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new IllegalArgumentException("Verification token has expired.");
        }

        // Enable the user
        user.setEnabled(true);
        userRepository.save(user);

        // Delete the token after successful verification
        verificationTokenRepository.delete(verificationToken);

        return "Email verified successfully. You can now log in.";
    }

    @Override
    public void sendPasswordResetRequest(PasswordResetDTO passwordResetDTO) {
        Optional<User> optionalUser = userRepository.findByEmail(passwordResetDTO.getEmail());
        User user = optionalUser.orElseThrow(() -> new ApiException("No user found with that email.", HttpStatusCode.valueOf(409)));

        // Create password reset token
        String token = UUID.randomUUID().toString();
        VerificationToken passwordResetToken = new VerificationToken(token, user, TokenType.PASSWORD_RESET);
        verificationTokenRepository.save(passwordResetToken);

        // Send reset password email
        String resetUrl = "http://104.248.46.170:8080/api/v1/auth/reset-password?token=" + token + "&newPassword=" + passwordResetDTO.getNewPassword();
        String subject = "Password Reset Request";
        String body = "Please click the following link to reset your password: " + resetUrl;
        mailService.sendEmail(user.getEmail(), subject, body);
    }

    @Override
    public String resetPassword(String token, String newPassword) {
        VerificationToken passwordResetToken = verificationTokenRepository.findByTokenAndTokenType(token, TokenType.PASSWORD_RESET);

        if (passwordResetToken == null) {
            throw new IllegalArgumentException("Invalid password reset token.");
        }

        User user = passwordResetToken.getUser();
        if (user == null) {
            throw new IllegalArgumentException("No user found for this token.");
        }

        // Check if token is expired
        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            throw new IllegalArgumentException("Password reset token has expired.");
        }

        // Update the user's password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Delete the password reset token after successful reset
        verificationTokenRepository.delete(passwordResetToken);

        return "Password has been reset successfully. You can now log in with your new password.";
    }
}
