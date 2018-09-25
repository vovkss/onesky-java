package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import javax.annotation.Nullable;
import java.net.http.HttpClient;
import java.time.Instant;

public final class OneSkyFilesApi extends AbstractOneSkyApi {

    public static final class ProjectFile {
        private final String name;
        private final int countOfStrings;
        private final @Nullable Long id;
        private final @Nullable String status;
        private final @Nullable Instant uploadedAt;

        public ProjectFile(final String name,
                           final int countOfStrings,
                           final @Nullable Long id,
                           final @Nullable String status,
                           final @Nullable Instant uploadedAt) {
            this.name = name;
            this.countOfStrings = countOfStrings;
            this.id = id;
            this.status = status;
            this.uploadedAt = uploadedAt;
        }
    }

    OneSkyFilesApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

}
