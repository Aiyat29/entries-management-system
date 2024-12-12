package kg.alatoo.entryapi.controllers;

import kg.alatoo.entryapi.dto.UserDTO;
import kg.alatoo.entryapi.dto.authorization.AuthLoginDto;
import kg.alatoo.entryapi.dto.authorization.AuthRegistrationDTO;
import kg.alatoo.entryapi.dto.authorization.JwtTokenDto;
import kg.alatoo.entryapi.dto.authorization.RefreshTokenRequestDTO;
import kg.alatoo.entryapi.dto.reset.PasswordResetDTO;
import kg.alatoo.entryapi.entities.tokens.RefreshToken;
import kg.alatoo.entryapi.repositories.UserRepository;
import kg.alatoo.entryapi.repositories.VerificationTokenRepository;
import kg.alatoo.entryapi.services.auth.AuthService;
import kg.alatoo.entryapi.services.auth.JwtService;
import kg.alatoo.entryapi.services.auth.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public UserDTO register(@Validated @RequestBody AuthRegistrationDTO authRegistrationDTO){
        return authService.register(authRegistrationDTO);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        try {
            String response = authService.verifyEmail(token);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            authService.sendPasswordResetRequest(passwordResetDTO);
            return ResponseEntity.ok("Password reset link has been sent to your email.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("newPassword") String newPassword) {
        try {
            String response = authService.resetPassword(token, newPassword);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public JwtTokenDto AuthenticateAndGetToken(@RequestBody AuthLoginDto authRequestDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()){
            System.out.println("Refresh Token sent to user");
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            return JwtTokenDto.builder()
                    .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                    .token(refreshToken.getToken())
                    .build();

        } else {
            throw new UsernameNotFoundException("invalid user request..!!");
        }
    }
    @PostMapping("/refreshToken")
    public JwtTokenDto refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(userInfo -> {
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    return JwtTokenDto.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken())
                            .build();
                }).orElseThrow(() -> new RuntimeException("Refresh Token is not in Database!!"));
    }

}
