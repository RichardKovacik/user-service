package sk.mvp.common.event;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record EventMetadata(
        String correlationId,
        String sourceService) {
}
