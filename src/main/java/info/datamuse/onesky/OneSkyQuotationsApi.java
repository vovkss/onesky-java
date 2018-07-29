package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import java.net.http.HttpClient;

/**
 * OneSky Quotations API wrapper.
 */
public final class OneSkyQuotationsApi extends AbstractOneSkyApi {

    OneSkyQuotationsApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    // TODO: implement

}
