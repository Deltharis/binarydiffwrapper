package exceptions;

public class WrongDiffContentsException extends RuntimeException {
    public WrongDiffContentsException() {
    }

    public WrongDiffContentsException(String message) {
        super(message);
    }

    public WrongDiffContentsException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongDiffContentsException(Throwable cause) {
        super(cause);
    }

    public WrongDiffContentsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
