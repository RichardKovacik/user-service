package sk.mvp.user_service.async.outbox.entity;

public enum OutBoxStatus {
    PENDING,
    PROCESSED,
    FAILED,
}
