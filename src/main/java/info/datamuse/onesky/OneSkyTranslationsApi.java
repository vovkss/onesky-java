package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static info.datamuse.onesky.OneSkyProjectsApi.PROJECTS_BY_ID_API_URL_TEMPLATE;
import static info.datamuse.onesky.internal.HttpUtils.*;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.net.http.HttpResponse.BodyHandlers.ofFile;
import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static java.util.Objects.requireNonNull;

public final class OneSkyTranslationsApi extends AbstractOneSkyApi {

    private static final String TRANSLATION_LOCALE_KEY_PARAM = "locale";
    private static final String TRANSLATION_SOURCE_FILE_NAME_KEY_PARAM = "source_file_name";
    private static final String TRANSLATION_EXPORT_FILE_NAME_KEY_PARAM = "export_file_name";

    private static final String TRANSLATIONS_BY_PROJECT_ID_API_URL_TEMPLATE = PROJECTS_BY_ID_API_URL_TEMPLATE + "/translations";

    private static final String TEXT_PLAIN_CONTENT_TYPE = "text/plain; charset=UTF-8";

    OneSkyTranslationsApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    public CompletableFuture<Path> export(final long projectId, final Locale locale, final String fileName, final Path path) {
        return export(projectId, locale, fileName, null, ofFile(path));
    }

    public CompletableFuture<InputStream> export(final long projectId, final Locale locale, final String fileName) {
        return export(projectId, locale, fileName, null, ofInputStream());
    }

    private <T> CompletableFuture<T> export(final long projectId,
                                            final Locale locale,
                                            final String fileName,
                                            final @Nullable String exportFileName,
                                            final HttpResponse.BodyHandler<T> httpResponseBodyHandler) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(TRANSLATION_LOCALE_KEY_PARAM, locale.toLanguageTag());
        parameters.put(TRANSLATION_SOURCE_FILE_NAME_KEY_PARAM, requireNonNull(fileName));
        if (exportFileName != null) {
            parameters.put(TRANSLATION_EXPORT_FILE_NAME_KEY_PARAM, exportFileName);
        }
        return
                apiRequest(
                        HTTP_GET,
                        noBody(),
                        httpResponseBodyHandler,
                        String.format(TRANSLATIONS_BY_PROJECT_ID_API_URL_TEMPLATE, projectId),
                        Map.of(CONTENT_TYPE_HEADER, TEXT_PLAIN_CONTENT_TYPE),
                        parameters,
                        HTTP_STATUS_OK);
    }
}
