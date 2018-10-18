package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import java.net.http.HttpClient;

public final class OneSkyImportTasksApi extends AbstractOneSkyApi { // TODO: implement

    /**
     * Import task status.
     */
    public enum ImportTaskStatus {
        /**
         * Completed task status.
         */
        COMPLETED,

        /**
         * Task in progress status.
         */
        IN_PROGRESS,

        /**
         * Failed task status.
         */
        FAILED
    }

    public static class ImportTask {
    }

    OneSkyImportTasksApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

}
