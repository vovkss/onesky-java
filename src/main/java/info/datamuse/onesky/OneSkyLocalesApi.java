package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

/**
 * OneSky Locales API wrapper.
 */
public final class OneSkyLocalesApi extends AbstractOneSkyApi {

    private static final String LOCALES_API_URL_TEMPLATE = API_BASE_URL + "/locales";

    OneSkyLocalesApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns a {@link CompletableFuture promise} for the list of all locales supported by OneSky.
     *
     * @return list of all locales (promise)
     */
    public CompletableFuture<List<Locale>> list() {
        return apiGetListOfObjectsRequest(
            LOCALES_API_URL_TEMPLATE,
            dataItem -> Locale.forLanguageTag(dataItem.getString("code"))
        );
    }

}
