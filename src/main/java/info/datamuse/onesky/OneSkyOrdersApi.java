package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import java.net.http.HttpClient;

/**
 * OneSky Orders API wrapper.
 */
public final class OneSkyOrdersApi extends AbstractOneSkyApi {

    OneSkyOrdersApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    // TODO: implement

}
