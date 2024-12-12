package kg.alatoo.entryapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
    private Long imageId;

    private String imageUrl;

    private LocalDateTime createdAt;

    private Long belongsToId;
}
