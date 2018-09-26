package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;
import info.datamuse.onesky.internal.JsonUtils;
import info.datamuse.onesky.internal.MultipartBodyEncoder;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static info.datamuse.onesky.OneSkyProjectsApi.PROJECTS_BY_ID_API_URL_TEMPLATE;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

public final class OneSkyFilesApi extends AbstractOneSkyApi {

    public enum FileStatus {
        COMPLETED,
        IN_PROGRESS,
        FAILED;

        private static Map<String, FileStatus> statusMap = Map.of(
                "completed", COMPLETED,
                "in-progress", IN_PROGRESS,
                "failed", FAILED
        );

        static FileStatus forValue(final String value) {
            return statusMap.get(value.toLowerCase());
        }
    }

    public enum FileFormat {
        IOS_STRINGS,
        IOS_STRINGSDICT_XML,
        GNU_PO,
        ANDROID_XML,
        ANDROID_JSON,
        JAVA_PROPERTIES,
        RUBY_YML,
        RUBY_YAML,
        FLASH_XML,
        GNU_POT,
        RRC,
        RESX,
        HIERARCHICAL_JSON,
        PHP,
        PHP_SHORT_ARRAY,
        PHP_VARIABLES,
        HTML,
        RESW,
        YML,
        YAML,
        ADEMPIERE_XML,
        IDEMPIERE_XML,
        QT_TS_XML,
        XLIFF,
        RESJSON,
        TMX,
        L10N,
        INI,
        REQUIREJS
    }

    public static final class File {
        private final String name;
        private final @Nullable Integer countOfStrings;
        private final @Nullable FileImport importStatus;
        private final @Nullable FileFormat format;
        private final @Nullable Locale locale;

        File(final String name,
             final @Nullable Integer countOfStrings,
             final @Nullable FileImport importStatus,
             final @Nullable FileFormat format,
             final @Nullable Locale locale) {

            this.name = requireNonNull(name);
            this.countOfStrings = countOfStrings;
            this.importStatus = importStatus;
            this.format = format;
            this.locale = locale;
        }

        public String getName() {
            return name;
        }

        @Nullable
        public Integer getCountOfStrings() {
            return countOfStrings;
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
        public String toString() {
            return "File{" +
                    "name='" + name + '\'' +
                    ", countOfStrings=" + countOfStrings +
                    ", importStatus=" + importStatus +
                    ", format=" + format +
                    ", locale=" + locale +
                    '}';
        }
    }

    public static final class FileImport {
        final long id;
        final @Nullable FileStatus status;
        final Instant lastImportedAt;

        FileImport(final long id, final @Nullable FileStatus status, final Instant lastImportedAt) {
            this.id = id;
            this.status = status;
            this.lastImportedAt = requireNonNull(lastImportedAt);
        }

        public long getId() {
            return id;
        }

        @Nullable
        public FileStatus getStatus() {
            return status;
        }

        public Instant getLastImportedAt() {
            return lastImportedAt;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof FileImport)) return false;
            final FileImport that = (FileImport) o;
            return id == that.id &&
                    status == that.status &&
                    Objects.equals(lastImportedAt, that.lastImportedAt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, status, lastImportedAt);
        }

        @Override
        public String toString() {
            return "FileImport{" +
                    "id=" + id +
                    ", status=" + status +
                    ", lastImportedAt=" + lastImportedAt +
                    '}';
        }
    }

    private static final String PROJECT_FILE_NAME_KEY = "name";
    private static final String PROJECT_FILE_FILE_NAME_KEY = "file_name";
    private static final String PROJECT_FILE_STRING_COUNT_KEY = "string_count";
    private static final String PROJECT_FILE_LAST_IMPORT_KEY = "last_import";
    private static final String PROJECT_FILE_UPLOADED_AT_TIMESTAMP_KEY = "uploaded_at_timestamp";

    private static final String PROJECT_FILE_UPLOAD_FILE_PARAM = "file";
    private static final String PROJECT_FILE_UPLOAD_FILE_FORMAT_PARAM = "file_format";
    private static final String PROJECT_FILE_UPLOAD_LOCALE_PARAM = "locale";
    private static final String PROJECT_FILE_UPLOAD_KEEP_ALL_STRINGS_PARAM = "is_keeping_all_strings";
    private static final String PROJECT_FILE_UPLOAD_ALLOW_ORIGINAL_TRANSLATION_PARAM = "is_allow_translation_same_as_original";

    private static final String PROJECT_FILE_FORMAT_KEY = "format";
    private static final String PROJECT_FILE_LANGUAGE_KEY = "language";
    private static final String PROJECT_FILE_LANGUAGE_CODE_KEY = "code";
    private static final String PROJECT_FILE_IMPORT_KEY = "import";

    private static final String PROJECT_FILE_IMPORT_ID_KEY = "id";
    private static final String PROJECT_FILE_IMPORT_STATUS_KEY = "status";
    private static final String PROJECT_FILE_IMPORT_CREATED_AT_TIMESTAMP_KEY = "created_at_timestamp";

    private static final String PROJECT_FILES_ID_API_URL_TEMPLATE = PROJECTS_BY_ID_API_URL_TEMPLATE + "/files";

    OneSkyFilesApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    public CompletableFuture<Page<File>> pagedList(final long projectId, long pageNumber, final long maxItemsPerPage) {
        return apiGetPagedListRequest(
                String.format(PROJECT_FILES_ID_API_URL_TEMPLATE, projectId),
                emptyMap(),
                pageNumber,
                maxItemsPerPage,
                data -> toFile(data)
        );
    }

    public CompletableFuture<File> upload(final long projectId,
                                          final FileFormat fileFormat,
                                          final String fileName,
                                          final InputStream inputStream,
                                          final @Nullable Locale locale) {
        return upload(projectId, fileFormat, fileName, inputStream, locale, true, false);
    }

    public CompletableFuture<File> upload(final long projectId,
                                          final FileFormat fileFormat,
                                          final String fileName,
                                          final InputStream inputStream,
                                          final @Nullable Locale locale,
                                          final boolean isKeepingAllStrings,
                                          final boolean isAllowTranslationSameAsOriginal) {
        final MultipartBodyEncoder multipartBodyEncoder = new MultipartBodyEncoder();
        multipartBodyEncoder.addFilePart(
                PROJECT_FILE_UPLOAD_FILE_PARAM,
                fileName,
                fileFormat.toString(),
                inputStream
        );
        multipartBodyEncoder.addFormField(PROJECT_FILE_UPLOAD_FILE_FORMAT_PARAM, String.valueOf(fileFormat));
        if (locale != null) {
            multipartBodyEncoder.addFormField(PROJECT_FILE_UPLOAD_LOCALE_PARAM, locale.toLanguageTag());
        }
        multipartBodyEncoder.addFormField(PROJECT_FILE_UPLOAD_KEEP_ALL_STRINGS_PARAM, String.valueOf(isKeepingAllStrings));
        multipartBodyEncoder.addFormField(PROJECT_FILE_UPLOAD_ALLOW_ORIGINAL_TRANSLATION_PARAM, String.valueOf(isAllowTranslationSameAsOriginal));

        return apiMultiPartRequest(
                String.format(PROJECT_FILES_ID_API_URL_TEMPLATE, projectId),
                HttpRequest.BodyPublishers.ofInputStream(() -> multipartBodyEncoder.finish()),
                multipartBodyEncoder.getBoundary(),
                Map.of(),
                data -> toFileAfterCreation(data)
        );
    }

    public CompletableFuture<Void> delete(final long projectId, final String fileName) {
        return apiDeleteRequest(
                String.format(Locale.ROOT, PROJECT_FILES_ID_API_URL_TEMPLATE, projectId),
                Map.of(PROJECT_FILE_FILE_NAME_KEY, fileName)
        );
    }

    private static File toFile(final JSONObject fileJson) {
        final @Nullable FileImport fileImport = JsonUtils.getOptionalJsonValue(fileJson, PROJECT_FILE_LAST_IMPORT_KEY, JSONObject.class,
                fileImportJson ->
                        new FileImport(
                                fileImportJson.getLong(PROJECT_FILE_IMPORT_ID_KEY),
                                FileStatus.forValue(fileImportJson.getString(PROJECT_FILE_IMPORT_STATUS_KEY)),
                                fileJson.isNull(PROJECT_FILE_UPLOADED_AT_TIMESTAMP_KEY) ?
                                        null : Instant.ofEpochMilli(fileJson.getLong(PROJECT_FILE_UPLOADED_AT_TIMESTAMP_KEY))
                        )
        );
        return new File(
                fileJson.getString(PROJECT_FILE_FILE_NAME_KEY),
                fileJson.getInt(PROJECT_FILE_STRING_COUNT_KEY),
                fileImport,
                null,
                null
        );
    }

    private static File toFileAfterCreation(final JSONObject fileJson) {
        final JSONObject fileLanguageJson = fileJson.getJSONObject(PROJECT_FILE_LANGUAGE_KEY);
        final JSONObject fileImportJson = fileJson.getJSONObject(PROJECT_FILE_IMPORT_KEY);

        final Locale locale = Locale.forLanguageTag(fileLanguageJson.getString(PROJECT_FILE_LANGUAGE_CODE_KEY));
        final FileImport fileImport = new FileImport(
                fileImportJson.getLong(PROJECT_FILE_IMPORT_ID_KEY),
                null,
                Instant.ofEpochMilli(fileImportJson.getLong(PROJECT_FILE_IMPORT_CREATED_AT_TIMESTAMP_KEY))
        );

        return new File(
                fileJson.getString(PROJECT_FILE_NAME_KEY),
                null,
                fileImport,
                Enum.valueOf(FileFormat.class, fileJson.getString(PROJECT_FILE_FORMAT_KEY)),
                locale
        );
    }

}
