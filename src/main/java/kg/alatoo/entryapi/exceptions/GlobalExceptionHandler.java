package kg.alatoo.entryapi.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatusCode().value())
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", ex.getMessage(),
                        "status", ex.getStatusCode().value()
                ));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Object> handleIOException(IOException ex) {
        return ResponseEntity.status(402)
                .body(Map.of(
                        "timestamp", LocalDateTime.now(),
                        "message", ex.getMessage(),
                        "status", 402
                ));
    }
}
