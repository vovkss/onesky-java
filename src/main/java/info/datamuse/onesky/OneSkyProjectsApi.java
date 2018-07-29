package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import java.net.http.HttpClient;

/**
 * OneSky Projects API wrapper.
 */
public final class OneSkyProjectsApi extends AbstractOneSkyApi {

    OneSkyProjectsApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    // TODO: implement

}
