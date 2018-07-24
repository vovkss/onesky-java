package info.datamuse.onesky.internal;

/**
 * HTTP utilities.
 */
public final class HttpUtils {

    /**
     * HTTP {@code POST} method name.
     */
    public static final String HTTP_POST = "POST";

    /**
     * HTTP {@code GET} method name.
     */
    public static final String HTTP_GET = "GET";

    /**
     * HTTP {@code PUT} method name.
     */
    public static final String HTTP_PUT = "PUT";

    /**
     * HTTP {@code DELETE} method name.
     */
    public static final String HTTP_DELETE = "DELETE";

    /**
     * HTTP response status code 200 ("OK").
     */
    public static final long HTTP_STATUS_OK = 200;

    /**
     * HTTP response status code 201 ("Created").
     */
    public static final long HTTP_STATUS_CREATED = 201;

    /**
     * {@code Content-Type} header name.
     */
    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    private HttpUtils() {
        // Namespace
    }

}
