package exceptions;

public class CantReadConfigFileException extends RuntimeException {
    public CantReadConfigFileException() {
    }

    public CantReadConfigFileException(String message) {
        super(message);
    }

    public CantReadConfigFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CantReadConfigFileException(Throwable cause) {
        super(cause);
    }

    public CantReadConfigFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
