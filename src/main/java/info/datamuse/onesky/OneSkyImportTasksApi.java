package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import java.net.http.HttpClient;

public final class OneSkyImportTasksApi extends AbstractOneSkyApi { // TODO: implement

    OneSkyImportTasksApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

}
