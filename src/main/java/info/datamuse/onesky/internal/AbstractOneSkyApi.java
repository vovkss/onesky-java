package info.datamuse.onesky.internal;

import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * Abstract base class for OneSky API Wrappers.
 */
public abstract class AbstractOneSkyApi {

    /**
     * OneSky API base URL
     */
    public static final String API_BASE_URL = "https://platform.api.onesky.io/1";

    private final String apiKey;
    private final String apiSecret;

    /**
     * Protected constructor.
     *
     * @param apiKey OneSky API public key
     * @param apiSecret OneSky API secret key
     */
    protected AbstractOneSkyApi(final String apiKey, final String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    protected final Object apiGetRequest(final String url, final Map<String, String> parameters) {
        return null; // TODO:
    }

    protected final Object apiGetRequest(final String url) {
        return apiGetRequest(url, emptyMap());
    }

}
