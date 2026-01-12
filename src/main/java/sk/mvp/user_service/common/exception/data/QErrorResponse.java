package sk.mvp.user_service.common.exception.data;

import java.io.Serializable;

public class QErrorResponse implements Serializable {
    private sk.mvp.user_service.common.exception.data.Error error;

    public QErrorResponse(sk.mvp.user_service.common.exception.data.Error error) {
        this.error = error;
    }

    public sk.mvp.user_service.common.exception.data.Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
