package sk.mvp.common.event;

public enum EventType {
    USER_REGITERED_EVENT,
    USER_LOGGED_IN_EVENT,
    PASSWORD_CHANGE_REQUESTED_EVENT,
    UNKNOWN_EVENT;

    public EventType fromString(String value) {
        try {
            return EventType.valueOf(value);
        } catch (Exception e) {
            return UNKNOWN_EVENT;
        }
    }
}
