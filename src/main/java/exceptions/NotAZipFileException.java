package exceptions;

public class NotAZipFileException extends RuntimeException {
    public NotAZipFileException() {
    }

    public NotAZipFileException(String message) {
        super(message);
    }

    public NotAZipFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotAZipFileException(Throwable cause) {
        super(cause);
    }

    public NotAZipFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
