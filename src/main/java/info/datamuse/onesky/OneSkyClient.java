package info.datamuse.onesky;

import java.net.http.HttpClient;

/**
 * <a href="http://oneskyapp.com/">OneSky</a> API client.
 *
 * @see <a href="https://github.com/onesky/api-documentation-platform/blob/master/README.md">OneSky Platform API documentation</a>
 */
public final class OneSkyClient {

    private final String apiKey;
    private final String apiSecret;
    private final HttpClient httpClient;

    /**
     * Client constructor.
     *
     * @param apiKey OneSky API public key
     * @param apiSecret OneSky API secret key
     * @param httpClient HTTP Client
     */
    public OneSkyClient(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.httpClient = httpClient;
    }

    /**
     * Returns Locales API wrapper.
     *
     * @return Locales API wrapper
     */
    public OneSkyLocalesApi locales() {
        return new OneSkyLocalesApi(apiKey, apiSecret, httpClient);
    }

}
