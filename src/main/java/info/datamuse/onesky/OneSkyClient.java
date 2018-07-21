package info.datamuse.onesky;

/**
 * <a href="http://oneskyapp.com/">OneSky</a> API client.
 *
 * @see <a href="https://github.com/onesky/api-documentation-platform/blob/master/README.md">OneSky Platform API documentation</a>
 */
public final class OneSkyClient {

    private final String apiKey;
    private final String apiSecret;

    /**
     * Client constructor.
     *
     * @param apiKey OneSky API public key
     * @param apiSecret OneSky API secret key
     */
    public OneSkyClient(final String apiKey, final String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    /**
     * Returns Locales API wrapper.
     *
     * @return Locales API wrapper
     */
    public OneSkyLocalesApi locales() {
        return new OneSkyLocalesApi(apiKey, apiSecret);
    }

}
