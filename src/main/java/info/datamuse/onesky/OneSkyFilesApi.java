package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import javax.annotation.Nullable;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public final class OneSkyFilesApi extends AbstractOneSkyApi {

    /**
     * OneSky File Information.
     */
    public static final class FileInfo {
        private final String name;
        private final @Nullable Long stringCount;
        private final @Nullable OneSkyImportTasksApi.ImportTaskStatus importStatus;
        private final @Nullable Instant uploaded;

        /**
         * File Information constructor.
         *
         * @param name file name
         * @param stringCount optional number of strings
         * @param importStatus optional import task status
         * @param uploaded optional upload instant
         */
        public FileInfo(
            final String name,
            final @Nullable Long stringCount,
            final @Nullable OneSkyImportTasksApi.ImportTaskStatus importStatus,
            final @Nullable Instant uploaded
        ) {
            this.name = requireNonNull(name);
            this.stringCount = stringCount;
            this.importStatus = importStatus;
            this.uploaded = uploaded;
        }

        /**
         * Returns file name.
         *
         * @return file name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns optional number of strings.
         *
         * @return optional number of strings
         */
        public @Nullable Long getStringCount() {
            return stringCount;
        }

        /**
         * Returns optional import task status.
         *
         * @return optional import task status
         */
        public @Nullable OneSkyImportTasksApi.ImportTaskStatus getImportStatus() {
            return importStatus;
        }

        /**
         * Returns optional upload instant.
         *
         * @return optional upload instant
         */
        public @Nullable Instant getUploaded() {
            return uploaded;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof FileInfo)) {
                return false;
            }
            final FileInfo theObj = (FileInfo) obj;
            return
                name.equals(theObj.name)
                && Objects.equals(stringCount, theObj.stringCount)
                && Objects.equals(importStatus, theObj.importStatus)
                && Objects.equals(uploaded, theObj.uploaded);
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return String.format(
                Locale.ROOT,
                "OneSkyFilesApi.FileInfo{name=%s}",
                name
            );
        }
    }

    OneSkyFilesApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    public CompletableFuture<OneSkyImportTasksApi.ImportTask> upload() {
        return null;
    }

    public CompletableFuture<Page<FileInfo>> pagedList(final long pageNumber, final long maxItemsPerPage) {
        return null;
    }

    public CompletableFuture<Void> delete() {
        return null;
    }

}
