package exceptions;

public class CantCreateHashException extends RuntimeException {

    public CantCreateHashException() {
    }

    public CantCreateHashException(String message) {
        super(message);
    }

    public CantCreateHashException(String message, Throwable cause) {
        super(message, cause);
    }

    public CantCreateHashException(Throwable cause) {
        super(cause);
    }

    public CantCreateHashException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
