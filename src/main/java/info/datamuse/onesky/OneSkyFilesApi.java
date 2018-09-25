package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;
import info.datamuse.onesky.internal.JsonUtils;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static info.datamuse.onesky.OneSkyProjectGroupsApi.PROJECT_GROUP_BY_ID_API_URL_TEMPLATE;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

public final class OneSkyFilesApi extends AbstractOneSkyApi {

    public static final class ProjectFile {
        private final String name;
        private final int countOfStrings;
        private final @Nullable
        FileImport fileImport;

        public ProjectFile(final String name,
                           final int countOfStrings,
                           final @Nullable FileImport fileImport) {
            this.name = requireNonNull(name);
            this.countOfStrings = requireNonNull(countOfStrings);
            this.fileImport = fileImport;
        }
    }

    public static final class FileImport {
        final long id;
        final @Nullable
        FileStatus status;
        final Instant uploadedAt;

        public FileImport(final long id, final @Nullable FileStatus status, final Instant uploadedAt) {
            this.id = id;
            this.status = status;
            this.uploadedAt = requireNonNull(uploadedAt);
        }
    }

    public enum FileStatus {
        COMPLETED,
        IN_PROGRESS,
        FAILED;

        private static Map<String, FileStatus> statusMap = Map.of(
                "completed", COMPLETED,
                "in-progress", IN_PROGRESS,
                "failed", FAILED
        );

        public static FileStatus forValue(final String value) {
            return statusMap.get(value.toLowerCase());
        }
    }

    public static final class UploadFileResponse {
        private final String name;
        private final @Nullable String format;
        private final Locale locale;
        private final FileImport fileImport;

        public UploadFileResponse(final String name, final @Nullable String format, final Locale locale, final FileImport fileImport) {
            this.name = requireNonNull(name);
            this.format = format;
            this.locale = requireNonNull(locale);
            this.fileImport = requireNonNull(fileImport);
        }
    }

    private static final String PROJECT_FILE_NAME_KEY = "name";
    private static final String PROJECT_FILE_STRING_COUNT_KEY = "string_count";
    private static final String PROJECT_FILE_LAST_IMPORT_KEY = "last_import";
    private static final String PROJECT_FILE_UPLOADED_AT_TIMESTAMP_KEY = "uploaded_at_timestamp";

    private static final String PROJECT_FILE_UPLOAD_FILE_PARAM = "file";
    private static final String PROJECT_FILE_UPLOAD_FILE_FORMAT_PARAM = "file_format";
    private static final String PROJECT_FILE_UPLOAD_LOCALE_PARAM = "locale";
    private static final String PROJECT_FILE_UPLOAD_KEEP_ALL_STRINGS_PARAM = "is_keeping_all_strings";
    private static final String PROJECT_FILE_UPLOAD_ALLOW_ORIGINAL_TRANSLATION_PARAM = "is_allow_translation_same_as_original";

    private static final String PROJECT_FILE_LAST_IMPORT_ID_KEY = "id";
    private static final String PROJECT_FILE_LAST_IMPORT_STATUS_KEY = "status";

    private static final String PROJECT_FILES_ID_API_URL_TEMPLATE = PROJECT_GROUP_BY_ID_API_URL_TEMPLATE + "/files";

    OneSkyFilesApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    public CompletableFuture<Page<ProjectFile>> pagedList(final long projectId, long pageNumber, final long maxItemsPerPage) {
        return apiGetPagedListRequest(
                String.format(PROJECT_FILES_ID_API_URL_TEMPLATE, projectId),
                emptyMap(),
                pageNumber,
                maxItemsPerPage,
                data -> toProjectFile(data)
        );
    }

    public CompletableFuture<String> upload(final long projectId,
                                                        final Supplier<? extends InputStream> streamSupplier,
                                                        final String fileFormat,
                                                        final @Nullable Locale locale) {
        return upload(projectId, streamSupplier, fileFormat, locale, true, false);
    }

    public CompletableFuture<String> upload(final long projectId,
                                                        final Supplier<? extends InputStream> streamSupplier,
                                                        final String fileFormat,
                                                        final @Nullable Locale locale,
                                                        final boolean isKeepingAllStrings,
                                                        final boolean isAllowTranslationSameAsOriginal) {
//        final Map<String, HttpRequest.BodyPublisher> parts = new LinkedHashMap<>();
//        parts.put(PROJECT_FILE_UPLOAD_FILE_PARAM, HttpRequest.BodyPublishers.ofInputStream(streamSupplier));
//        parts.put(PROJECT_FILE_UPLOAD_FILE_FORMAT_PARAM, HttpRequest.BodyPublishers.ofString(String.valueOf(fileFormat)));
//        if (locale != null) {
//            parts.put(PROJECT_FILE_UPLOAD_LOCALE_PARAM, HttpRequest.BodyPublishers.ofString(locale.toLanguageTag()));
//        }
//        parts.put(PROJECT_FILE_UPLOAD_KEEP_ALL_STRINGS_PARAM, HttpRequest.BodyPublishers.ofString(String.valueOf(isKeepingAllStrings)));
//        parts.put(PROJECT_FILE_UPLOAD_ALLOW_ORIGINAL_TRANSLATION_PARAM, HttpRequest.BodyPublishers.ofString(String.valueOf(isAllowTranslationSameAsOriginal)));
//
//        final HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
//        requestBuilder.uri(URI.create(String.format(PROJECT_FILES_ID_API_URL_TEMPLATE, projectId)));
//        requestBuilder.setHeader("Content-Type","multipart/form-data");
//        parts.entrySet().stream().forEach(entry -> {
//            requestBuilder.setHeader("boundary", entry.getKey());
//            requestBuilder.POST(entry.getValue());
//        });
        return null;
    }

    private static ProjectFile toProjectFile(final JSONObject projectFileJson) {
        final @Nullable FileImport projectFileLastImport = JsonUtils.getOptionalJsonValue(projectFileJson, PROJECT_FILE_LAST_IMPORT_KEY, JSONObject.class,
                fileLastImportJson ->
                        new FileImport(
                                fileLastImportJson.getLong(PROJECT_FILE_LAST_IMPORT_ID_KEY),
                                FileStatus.forValue(fileLastImportJson.getString(PROJECT_FILE_LAST_IMPORT_STATUS_KEY)),
                                fileLastImportJson.isNull(PROJECT_FILE_UPLOADED_AT_TIMESTAMP_KEY) ?
                                        null : Instant.ofEpochMilli(Long.valueOf(fileLastImportJson.getString(PROJECT_FILE_UPLOADED_AT_TIMESTAMP_KEY)))
                )
        );
        return new ProjectFile(
                projectFileJson.getString(PROJECT_FILE_NAME_KEY),
                projectFileJson.getInt(PROJECT_FILE_STRING_COUNT_KEY),
                projectFileLastImport
        );
    }

}
