package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import java.net.http.HttpClient;

/**
 * OneSky Translations API wrapper.
 */
public final class OneSkyTranslationsApi extends AbstractOneSkyApi {

    OneSkyTranslationsApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    // TODO: implement

}
