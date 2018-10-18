package info.datamuse.onesky;

import info.datamuse.onesky.OneSkyFilesApi.*;
import info.datamuse.onesky.internal.AbstractOneSkyApi;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static info.datamuse.onesky.OneSkyFilesApi.*;
import static info.datamuse.onesky.OneSkyProjectsApi.PROJECTS_BY_ID_API_URL_TEMPLATE;
import static info.datamuse.onesky.internal.JsonUtils.getOptionalJsonValue;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

public final class OneSkyImportTasksApi extends AbstractOneSkyApi {

    public static final class ImportTask {
        private final long id;
        private final String fileName;
        private final @Nullable Integer countOfStrings;
        private final @Nullable Integer countOfWords;
        private final @Nullable FileImport importStatus;
        private final @Nullable FileFormat format;
        private final @Nullable Locale locale;

        public ImportTask(final long id,
                          final String fileName,
                          final @Nullable Integer countOfStrings,
                          final @Nullable Integer countOfWords,
                          final @Nullable FileImport importStatus,
                          final @Nullable FileFormat format,
                          final @Nullable Locale locale) {
            this.id = id;
            this.fileName = requireNonNull(fileName);
            this.countOfStrings = countOfStrings;
            this.countOfWords = countOfWords;
            this.importStatus = importStatus;
            this.format = format;
            this.locale = locale;
        }

        public long getId() {
            return id;
        }

        public String getFileName() {
            return fileName;
        }

        @Nullable
        public Integer getCountOfStrings() {
            return countOfStrings;
        }

        @Nullable
        public Integer getCountOfWords() {
            return countOfWords;
        }

        @Nullable
        public FileImport getImportStatus() {
            return importStatus;
        }

        @Nullable
        public FileFormat getFormat() {
            return format;
        }

        @Nullable
        public Locale getLocale() {
            return locale;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImportTask that = (ImportTask) o;
            return id == that.id &&
                    Objects.equals(fileName, that.fileName) &&
                    Objects.equals(countOfStrings, that.countOfStrings) &&
                    Objects.equals(countOfWords, that.countOfWords) &&
                    Objects.equals(importStatus, that.importStatus) &&
                    format == that.format &&
                    Objects.equals(locale, that.locale);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "ImportTask{" +
                    "id=" + id +
                    ", fileName='" + fileName + '\'' +
                    ", countOfStrings=" + countOfStrings +
                    ", countOfWords=" + countOfWords +
                    ", importStatus=" + importStatus +
                    ", format=" + format +
                    ", locale=" + locale +
                    '}';
        }
    }

    private static final String PROJECT_IMPORT_TASKS_ID_API_URL_TEMPLATE = PROJECTS_BY_ID_API_URL_TEMPLATE + "/import-tasks";
    private static final String PROJECT_IMPORT_TASKS_IMPORT_ID_API_URL_TEMPLATE = PROJECT_IMPORT_TASKS_ID_API_URL_TEMPLATE + "/%d";

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

    public CompletableFuture<Page<ImportTask>> pagedList(final long projectId,
                                                                  final @Nullable FileStatus fileStatus,
                                                                  long pageNumber,
                                                                  final long maxItemsPerPage) {
        final Map<String, String> params = fileStatus == null ?
                emptyMap() : Map.of(PROJECT_FILE_IMPORT_STATUS_KEY, FileStatus.getValue(fileStatus));
        return apiGetPagedListRequest(
                String.format(PROJECT_IMPORT_TASKS_ID_API_URL_TEMPLATE, projectId),
                params,
                pageNumber,
                maxItemsPerPage,
                data -> toImportTask(data)
        );
    }

    public CompletableFuture<ImportTask> retrieve(final long projectId, final long importId) {
        return apiGetObjectRequest(
                String.format(Locale.ROOT, PROJECT_IMPORT_TASKS_IMPORT_ID_API_URL_TEMPLATE, projectId, importId),
                emptyMap(),
                data -> toImportTask(data)
        );
    }

    private static ImportTask toImportTask(final JSONObject importTaskJson) {
        final JSONObject fileJson = importTaskJson.getJSONObject(PROJECT_FILE_UPLOAD_FILE_PARAM);

        final @Nullable FileFormat fileFormat = getOptionalJsonValue(fileJson, PROJECT_FILE_FORMAT_KEY, String.class,
                formatJson -> Enum.valueOf(FileFormat.class, formatJson));
        final @Nullable Locale locale = getOptionalJsonValue(fileJson, PROJECT_FILE_UPLOAD_LOCALE_PARAM, JSONObject.class,
                localeJson -> Locale.forLanguageTag(localeJson.getString(PROJECT_FILE_LANGUAGE_CODE_KEY)));

        return new ImportTask(
                importTaskJson.getLong(PROJECT_FILE_IMPORT_ID_KEY),
                fileJson.getString(PROJECT_FILE_NAME_KEY),
                getOptionalJsonValue(importTaskJson, PROJECT_FILE_STRING_COUNT_KEY, Integer.class),
                getOptionalJsonValue(importTaskJson, PROJECT_FILE_WORD_COUNT_KEY, Integer.class),
                new OneSkyFilesApi.FileImport(
                        importTaskJson.getLong(PROJECT_FILE_IMPORT_ID_KEY),
                        FileStatus.forValue(importTaskJson.getString(PROJECT_FILE_IMPORT_STATUS_KEY)),
                        Instant.ofEpochSecond(importTaskJson.getLong(PROJECT_FILE_IMPORT_CREATED_AT_TIMESTAMP_KEY))
                        ),
                fileFormat,
                locale
        );
    }

}
