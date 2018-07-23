package info.datamuse.onesky;

/**
 * Represents a OneSky API call failure.
 */
public final class OneSkyApiException extends RuntimeException {
    private static final long serialVersionUID = 4653778862654645578L;

    /**
     * Constructs a new {@code OneSkyApiException} with the specified detail message.
     *
     * @param message the detail message
     */
    public OneSkyApiException(final String message) {
        super(message);
    }

    /**
     * Constructs a new {@code OneSkyApiException} with the specified cause.
     *
     * @param cause the cause, see {@link Throwable#getCause()}
     */
    public OneSkyApiException(final Exception cause) {
        super(cause);
    }

}
