package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import java.util.List;
import java.util.Locale;

/**
 * OneSky Locales API wrapper.
 */
public final class OneSkyLocalesApi extends AbstractOneSkyApi {

    private static final String LOCALES_API_URL_TEMPLATE = API_BASE_URL + "/locales";

    OneSkyLocalesApi(final String apiKey, final String apiSecret) {
        super(apiKey, apiSecret);
    }

    /**
     * Returns list of all locales supported by OneSky.
     *
     * @return list of all locales
     */
    public List<Locale> list() {
        apiGetRequest(LOCALES_API_URL_TEMPLATE);
        return null; // TODO:
    }

}
