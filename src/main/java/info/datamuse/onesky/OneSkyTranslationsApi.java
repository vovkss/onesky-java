package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static info.datamuse.onesky.OneSkyFilesApi.PROJECT_FILE_LANGUAGE_CODE_KEY;
import static info.datamuse.onesky.OneSkyProjectsApi.PROJECTS_BY_ID_API_URL_TEMPLATE;
import static info.datamuse.onesky.internal.HttpUtils.*;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpResponse.BodyHandlers.ofFile;
import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static java.util.Objects.requireNonNull;

public final class OneSkyTranslationsApi extends AbstractOneSkyApi {

    public static final class TranslationStatus {
        private final String fileName;
        private final Locale locale;
        private final short progress;
        private final int countOfStrings;
        private final int wordOfCounts;

        TranslationStatus(final String fileName,
                          final Locale locale,
                          final short progress,
                          final int countOfStrings,
                          final int wordOfCounts) {
            this.fileName = fileName;
            this.locale = locale;
            this.progress = progress;
            this.countOfStrings = countOfStrings;
            this.wordOfCounts = wordOfCounts;
        }

        public String getFileName() {
            return fileName;
        }

        public Locale getLocale() {
            return locale;
        }

        public short getProgress() {
            return progress;
        }

        public int getCountOfStrings() {
            return countOfStrings;
        }

        public int getWordOfCounts() {
            return wordOfCounts;
        }

        @Override
        public String toString() {
            return "TranslationStatus{" +
                    "fileName='" + fileName + '\'' +
                    ", locale=" + locale +
                    ", progress=" + progress +
                    ", countOfStrings=" + countOfStrings +
                    ", wordOfCounts=" + wordOfCounts +
                    '}';
        }
    }

    private static final String TRANSLATION_LOCALE_PARAM = "locale";
    private static final String TRANSLATION_SOURCE_FILE_NAME_PARAM = "source_file_name";
    private static final String TRANSLATION_EXPORT_FILE_NAME_PARAM = "export_file_name";
    private static final String TRANSLATION_FILE_FORMAT_NAME_PARAM = "file_format";

    private static final String TRANSLATION_FILE_NAME_KEY = "file_name";
    private static final String TRANSLATION_PROGRESS_KEY = "progress";
    private static final String TRANSLATION_STRING_COUNT_KEY = "string_count";
    private static final String TRANSLATION_WORD_COUNT_KEY = "word_count";

    private static final String TRANSLATIONS_BY_PROJECT_ID_API_URL_TEMPLATE = PROJECTS_BY_ID_API_URL_TEMPLATE + "/translations";
    private static final String TRANSLATIONS_MULTILINGUAL_BY_PROJECT_ID_API_URL_TEMPLATE = PROJECTS_BY_ID_API_URL_TEMPLATE + "/translations/multilingual";
    private static final String TRANSLATIONS_STATUS_BY_PROJECT_ID_API_URL_TEMPLATE = PROJECTS_BY_ID_API_URL_TEMPLATE + "/translations/status";

    private static final String TEXT_PLAIN_CONTENT_TYPE = "text/plain; charset=UTF-8";

    OneSkyTranslationsApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    public CompletableFuture<Path> export(final long projectId, final Locale locale, final String fileName, final Path path) {
        final String apiUrl = String.format(TRANSLATIONS_BY_PROJECT_ID_API_URL_TEMPLATE, projectId);
        final Map<String, String> parameters = Map.of(
                TRANSLATION_LOCALE_PARAM, locale.toLanguageTag(),
                TRANSLATION_SOURCE_FILE_NAME_PARAM, requireNonNull(fileName),
                TRANSLATION_EXPORT_FILE_NAME_PARAM, path.getFileName().toString()
        );
        return export(apiUrl, parameters, ofFile(path));
    }

    public CompletableFuture<InputStream> export(final long projectId, final Locale locale, final String fileName) {
        final String apiUrl = String.format(TRANSLATIONS_BY_PROJECT_ID_API_URL_TEMPLATE, projectId);
        final Map<String, String> parameters = Map.of(
                TRANSLATION_LOCALE_PARAM, locale.toLanguageTag(),
                TRANSLATION_SOURCE_FILE_NAME_PARAM, requireNonNull(fileName)
        );
        return export(apiUrl, parameters, ofInputStream());
    }

    public CompletableFuture<Path> exportMultilingual(final long projectId, final String fileName, final Path path, final @Nullable String format) {
        final String apiUrl = String.format(TRANSLATIONS_MULTILINGUAL_BY_PROJECT_ID_API_URL_TEMPLATE, projectId);
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(TRANSLATION_SOURCE_FILE_NAME_PARAM, requireNonNull(fileName));
        parameters.put(TRANSLATION_EXPORT_FILE_NAME_PARAM, path.getFileName().toString());
        if (format != null) {
            parameters.put(TRANSLATION_FILE_FORMAT_NAME_PARAM, format);
        }
        return export(apiUrl, parameters, ofFile(path));
    }

    public CompletableFuture<InputStream> exportMultilingual(final long projectId, final String fileName, final @Nullable String format) {
        final String apiUrl = String.format(TRANSLATIONS_MULTILINGUAL_BY_PROJECT_ID_API_URL_TEMPLATE, projectId);
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(TRANSLATION_SOURCE_FILE_NAME_PARAM, requireNonNull(fileName));
        if (format != null) {
            parameters.put(TRANSLATION_FILE_FORMAT_NAME_PARAM, format);
        }
        return export(apiUrl, parameters, ofInputStream());
    }

    private <T> CompletableFuture<T> export(final String apiUrl,
                                            final Map<String, String> parameters,
                                            final HttpResponse.BodyHandler<T> httpResponseBodyHandler) {
        return
                apiRequest(
                        HTTP_GET,
                        noBody(),
                        httpResponseBodyHandler,
                        apiUrl,
                        Map.of(CONTENT_TYPE_HEADER, TEXT_PLAIN_CONTENT_TYPE),
                        parameters,
                        HTTP_STATUS_OK);
    }

    public CompletableFuture<TranslationStatus> status(final long projectId, final String fileName, final Locale locale) {
        return apiGetObjectRequest(
                String.format(TRANSLATIONS_STATUS_BY_PROJECT_ID_API_URL_TEMPLATE, projectId),
                Map.of(
                        TRANSLATION_FILE_NAME_KEY, requireNonNull(fileName),
                        TRANSLATION_LOCALE_PARAM, locale.toLanguageTag()
                        ),
                statusJson -> toTranslationStatus(statusJson)
        );
    }

    private static final TranslationStatus toTranslationStatus(final JSONObject statusJson) {
        return new TranslationStatus(
                statusJson.getString(TRANSLATION_FILE_NAME_KEY),
                Locale.forLanguageTag(
                        statusJson.getJSONObject(TRANSLATION_LOCALE_PARAM)
                                .getString(PROJECT_FILE_LANGUAGE_CODE_KEY)
                ),
                Short.valueOf(statusJson.getString(TRANSLATION_PROGRESS_KEY)
                        .trim()
                        .replace("%", "")
                ),
                statusJson.getInt(TRANSLATION_STRING_COUNT_KEY),
                statusJson.getInt(TRANSLATION_WORD_COUNT_KEY)
        );
    }
}
