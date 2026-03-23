package sk.mvp.common.payloads;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record UserRegisteredPayload(
        String email,
        String link) {
}
