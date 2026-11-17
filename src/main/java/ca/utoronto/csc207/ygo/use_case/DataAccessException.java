package ca.utoronto.csc207.ygo.use_case;

/** An expected API or storage failure translated by an outer adapter. */
public final class DataAccessException extends Exception {
    private static final long serialVersionUID = 1L;

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
