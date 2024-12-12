package kg.alatoo.entryapi.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class EntryDTO {
    private Long entryId;

    private Long createdById;

    private String name;

    private String content;

    private Double latitude;

    private Double longitude;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
