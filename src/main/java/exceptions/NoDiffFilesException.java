package exceptions;

public class NoDiffFilesException extends RuntimeException {
    public NoDiffFilesException() {
    }

    public NoDiffFilesException(String message) {
        super(message);
    }

    public NoDiffFilesException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDiffFilesException(Throwable cause) {
        super(cause);
    }

    public NoDiffFilesException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
