package exception;

public class AlreadyLoggedOutException extends Exception {
    public AlreadyLoggedOutException(String message) {
        super(message);
    }
}
