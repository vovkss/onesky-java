package info.datamuse.onesky;

import java.net.http.HttpClient;

import static java.util.Objects.requireNonNull;

/**
 * <a href="http://oneskyapp.com/">OneSky</a> API client.
 *
 * @see <a href="https://github.com/onesky/api-documentation-platform/blob/master/README.md">OneSky Platform API documentation</a>
 */
public final class OneSkyClient { // CHECKSTYLE:Factory

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
        this.apiKey = requireNonNull(apiKey);
        this.apiSecret = requireNonNull(apiSecret);
        this.httpClient = requireNonNull(httpClient);
    }

    /**
     * Returns Project Groups API wrapper.
     *
     * @return Project Groups API wrapper
     */
    public OneSkyProjectGroupsApi projectGroups() {
        return new OneSkyProjectGroupsApi(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns Projects API wrapper.
     *
     * @return Projects API wrapper
     */
    public OneSkyProjectsApi projects() {
        return new OneSkyProjectsApi(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns Project Types API wrapper.
     *
     * @return Project Types API wrapper
     */
    public OneSkyProjectTypesApi projectTypes() {
        return new OneSkyProjectTypesApi(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns Files API wrapper.
     *
     * @return Files API wrapper
     */
    public OneSkyFilesApi files() {
        return new OneSkyFilesApi(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns Translations API wrapper.
     *
     * @return Translations API wrapper
     */
    public OneSkyTranslationsApi translations() {
        return new OneSkyTranslationsApi(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns Import Tasks API wrapper.
     *
     * @return Import Tasks API wrapper
     */
    public OneSkyImportTasksApi importTasks() {
        return new OneSkyImportTasksApi(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns Screenshots API wrapper.
     *
     * @return Screenshots API wrapper
     */
    public OneSkyScreenshotsApi screenshots() {
        return new OneSkyScreenshotsApi(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns Quotations API wrapper.
     *
     * @return Quotations API wrapper
     */
    public OneSkyQuotationsApi quotations() {
        return new OneSkyQuotationsApi(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns Orders API wrapper.
     *
     * @return Orders API wrapper
     */
    public OneSkyOrdersApi orders() {
        return new OneSkyOrdersApi(apiKey, apiSecret, httpClient);
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
