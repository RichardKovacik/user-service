package sk.mvp.common.payloads;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
public record PasswordResetPayload(
        String email,
        String link) {

}
