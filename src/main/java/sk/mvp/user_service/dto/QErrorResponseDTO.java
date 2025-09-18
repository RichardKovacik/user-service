package sk.mvp.user_service.dto;

import java.io.Serializable;

public class QErrorResponseDTO implements Serializable {
    private Error error;

    public QErrorResponseDTO(Error error) {
        this.error = error;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
