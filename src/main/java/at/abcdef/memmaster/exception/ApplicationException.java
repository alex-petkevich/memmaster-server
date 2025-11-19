package at.abcdef.memmaster.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable cause) {}

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
