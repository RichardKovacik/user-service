package sk.mvp.user_service.common.exception.data;

import java.io.Serializable;

public class QErrorResponse implements Serializable {
    private QError QError;

    public QErrorResponse(QError QError) {
        this.QError = QError;
    }

    public QError getError() {
        return QError;
    }

    public void setError(QError QError) {
        this.QError = QError;
    }
}
