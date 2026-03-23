package sk.mvp.user_service.auth.service.impl;

import org.springframework.stereotype.Service;
import sk.mvp.user_service.auth.service.IVerificationTokenService;
import sk.mvp.user_service.common.exception.QApplicationException;
import sk.mvp.user_service.common.exception.data.ErrorType;
import sk.mvp.user_service.entity.User;
import sk.mvp.user_service.entity.VerificationToken;
import sk.mvp.user_service.user.repository.VerificationTokenRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class VerificationTokenImpl implements IVerificationTokenService {
    private VerificationTokenRepository repository;

    public VerificationTokenImpl(VerificationTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public VerificationToken getVerificationToken(String token) {
        return this.repository.findByToken(token).orElseThrow(() -> new QApplicationException(ErrorType.VERIFICATION_TOKEN_INVALID));
    }

    @Override
    public VerificationToken createVerificationToken(User user) {
        Instant expiresAt = Instant.now().plus(Duration.ofDays(2));
        VerificationToken verificationToken = new VerificationToken(UUID.randomUUID().toString(), expiresAt, user);
        // save verificationToken to DB
        return this.repository.save(verificationToken);
    }

    @Override
    public void deleteVerificationToken(String token) {

    }

    @Override
    public void invalidateVerificationToken(String token) {

    }
}
