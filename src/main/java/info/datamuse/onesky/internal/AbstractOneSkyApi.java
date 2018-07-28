package info.datamuse.onesky.internal;

import info.datamuse.onesky.OneSkyApiException;
import info.datamuse.onesky.Page;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static info.datamuse.onesky.internal.HttpUtils.CONTENT_TYPE_HEADER;
import static info.datamuse.onesky.internal.HttpUtils.HTTP_DELETE;
import static info.datamuse.onesky.internal.HttpUtils.HTTP_GET;
import static info.datamuse.onesky.internal.HttpUtils.HTTP_POST;
import static info.datamuse.onesky.internal.HttpUtils.HTTP_STATUS_CREATED;
import static info.datamuse.onesky.internal.HttpUtils.HTTP_STATUS_OK;
import static info.datamuse.onesky.internal.JsonUtils.getOptionalJsonValue;
import static info.datamuse.onesky.internal.JsonUtils.unexpectedJsonTypeException;
import static java.net.http.HttpRequest.BodyPublishers.noBody;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Abstract base class for OneSky API Wrappers.
 */
public abstract class AbstractOneSkyApi {

    /**
     * OneSky API base URL.
     */
    protected static final String API_BASE_URL = "https://platform.api.onesky.io/1";

    private static final String PAGE_NUMBER_PARAM = "page";
    private static final String PAGE_SIZE_PARAM = "per_page";
    private static final long MAX_PAGE_SIZE = 100;

    private static final String RESPONSE_META_KEY = "meta";
    private static final String RESPONSE_DATA_KEY = "data";
    private static final String TOTAL_ITEMS_COUNT_KEY = "record_count";
    private static final String TOTAL_PAGES_COUNT_KEY = "page_count";

    private static final Logger logger = getLogger(AbstractOneSkyApi.class);

    private final String apiKey;
    private final String apiSecret;
    private final HttpClient httpClient;

    /**
     * Protected constructor.
     *
     * @param apiKey OneSky API public key
     * @param apiSecret OneSky API secret key
     * @param httpClient HTTP Client
     */
    protected AbstractOneSkyApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.httpClient = httpClient;
    }

    // TODO: implement and self-review the below

    protected final <T> CompletableFuture<T> apiGetObjectRequest(
        final String apiUrl,
        final Map<String, String> parameters,
        final Function<JSONObject, T> dataConverter
    ) {
        return
            apiDataRequest(HTTP_GET, noBody(), apiUrl, parameters, HTTP_STATUS_OK)
                .thenApply(data -> {
                    if (!(data instanceof JSONObject)) {
                        throw unexpectedJsonTypeException(RESPONSE_DATA_KEY, data, JSONObject.class);
                    }
                    return dataConverter.apply((JSONObject) data);
                });
    }

    /**
     * Executes an API GET-request which is expected to return a list and returns a {@link CompletableFuture promise} for the result list.
     *
     * @param <T> target list item type
     * @param apiUrl API URL excluding query parameters
     * @param parameters URL query parameters excluding auth-parameters
     * @param dataItemConverter converter of the result-data-list-item-JSONs into target types.
     * @return retrieved list (promise)
     */
    protected final <T> CompletableFuture<List<T>> apiGetListRequest(
        final String apiUrl,
        final Map<String, String> parameters,
        final Function<JSONObject, T> dataItemConverter
    ) {
        return
            apiDataRequest(HTTP_GET, noBody(), apiUrl, parameters, HTTP_STATUS_OK)
                .thenApply(data -> {
                    if (!(data instanceof JSONArray)) {
                        throw unexpectedJsonTypeException(RESPONSE_DATA_KEY, data, JSONArray.class);
                    }
                    return getResponseDataList((JSONArray) data, dataItemConverter);
                });
    }

    protected final <T> CompletableFuture<Page<T>> apiGetPagedListRequest(
        final String apiUrl,
        final Map<String, String> parameters,
        final long pageNumber,
        final long maxItemsPerPage,
        final Function<JSONObject, T> dataItemConverter
    ) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "`pageNumber` must be positive, but was: %d", pageNumber));
        }
        if (maxItemsPerPage < 1) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "`maxItemsPerPage` must be positive, but was: %d", maxItemsPerPage));
        }
        if (maxItemsPerPage > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "`maxItemsPerPage` must be <=%d, but was: %d", MAX_PAGE_SIZE, maxItemsPerPage));
        }

        final Map<String, String> parametersWithPaging = new HashMap<>(parameters);
        parametersWithPaging.put(PAGE_NUMBER_PARAM, Long.toString(pageNumber));
        parametersWithPaging.put(PAGE_SIZE_PARAM, Long.toString(maxItemsPerPage));

        return
            apiJsonRequest(HTTP_GET, noBody(), apiUrl, parametersWithPaging, HTTP_STATUS_OK)
                .thenApply(responseJson -> {
                    try {
                        final JSONObject metaJson = responseJson.getJSONObject(RESPONSE_META_KEY);
                        return new Page<>(
                            getResponseDataList(responseJson.getJSONArray(RESPONSE_DATA_KEY), dataItemConverter),
                            pageNumber,
                            maxItemsPerPage,
                            metaJson.getLong(TOTAL_ITEMS_COUNT_KEY),
                            metaJson.getLong(TOTAL_PAGES_COUNT_KEY)
                        );
                    } catch (final JSONException e) {
                        throw new OneSkyApiException(e);
                    }
                });
    }

    protected final <T> CompletableFuture<T> apiCreateRequest(
        final String apiUrl,
        final Map<String, String> parameters,
        final Function<JSONObject, T> dataConverter
    ) {
        return
            apiDataRequest(HTTP_POST, noBody(), apiUrl, parameters, HTTP_STATUS_CREATED)
                .thenApply(data -> {
                    if (!(data instanceof JSONObject)) {
                        throw unexpectedJsonTypeException(RESPONSE_DATA_KEY, data, JSONObject.class);
                    }
                    return dataConverter.apply((JSONObject) data);
                });
    }

    protected final CompletableFuture<Void> apiDeleteRequest(
        final String apiUrl,
        final Map<String, String> parameters
    ) {
        return
            apiRequest(HTTP_DELETE, noBody(), apiUrl, parameters, HTTP_STATUS_OK)
                .thenApply(responseJson -> null);
    }

    /**
     * Executes an API request and returns a {@link CompletableFuture promise} for the result's {@code data} part.
     *
     * @param httpMethod HTTP method name
     * @param httpRequestBodyPublisher HTTP request body publisher
     * @param apiUrl API URL excluding query parameters
     * @param parameters URL query parameters excluding auth-parameters
     * @param expectedStatus expected HTTP response status on success
     * @return retrieved data (promise)
     */
    protected final CompletableFuture<?> apiDataRequest(
        final String httpMethod,
        final HttpRequest.BodyPublisher httpRequestBodyPublisher,
        final String apiUrl,
        final Map<String, String> parameters,
        final int expectedStatus
    ) {
        return
            apiJsonRequest(httpMethod, httpRequestBodyPublisher, apiUrl, parameters, expectedStatus)
                .thenApply(responseJson -> responseJson.get(RESPONSE_DATA_KEY));
    }

    /**
     * Executes an API request and returns a {@link CompletableFuture promise} for the result body.
     *
     * @param httpMethod HTTP method name
     * @param httpRequestBodyPublisher HTTP request body publisher
     * @param apiUrl API URL excluding query parameters
     * @param parameters URL query parameters excluding auth-parameters
     * @param expectedStatus expected HTTP response status on success
     * @return retrieved data (promise)
     */
    protected final CompletableFuture<JSONObject> apiJsonRequest(
        final String httpMethod,
        final HttpRequest.BodyPublisher httpRequestBodyPublisher,
        final String apiUrl,
        final Map<String, String> parameters,
        final int expectedStatus
    ) {
        return
            apiRequest(httpMethod, httpRequestBodyPublisher, apiUrl, parameters, expectedStatus)
                .thenApply(httpResponseBody -> {
                    try {
                        final JSONObject responseJson = new JSONObject(httpResponseBody);
                        checkSuccessResponse(responseJson, expectedStatus);
                        return responseJson;
                    } catch (final JSONException e) {
                        throw new OneSkyApiException(e);
                    }
                });
    }

    protected final CompletableFuture<String> apiRequest(
        final String httpMethod,
        final HttpRequest.BodyPublisher httpRequestBodyPublisher,
        final String apiUrl,
        final Map<String, String> parameters,
        final int expectedStatus
    ) {
        logger.info("OneSky API call"); // TODO: include url with parameters, excluding auth data
        // TODO: log from within the Future, when finished
        final String apiUrlWithParameters =
            apiUrl
            + '?'
            + Stream.concat(
                parameters.entrySet().stream(),
                generateAuthParameters().entrySet().stream()
            ).map(
                paramEntry -> paramEntry.getKey() + '=' + paramEntry.getValue()
            ).collect(joining("&"));
        final HttpRequest apiHttpRequest =
            HttpRequest.newBuilder(URI.create(apiUrlWithParameters))
                .method(httpMethod, httpRequestBodyPublisher)
                .header(CONTENT_TYPE_HEADER, "application/json")
                .build();
        return
            httpClient
                .sendAsync(apiHttpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(httpResponse -> {
                    final int httpStatus = httpResponse.statusCode();
                    if (httpStatus != expectedStatus) {
                        throw new OneSkyApiException(String.format(Locale.ROOT, "Expected status=%d, but API responded with status=%d", expectedStatus, httpStatus));
                    }
                    return httpResponse.body();
                });
    }

    private Map<String, String> generateAuthParameters() {
        final String timestamp = Long.toString(Instant.now().getEpochSecond());
        final String devHash = md5Hex(timestamp + apiSecret);
        return Map.of(
            "api_key", apiKey,
            "timestamp", timestamp,
            "dev_hash", devHash
        );
    }

    private static void checkSuccessResponse(final JSONObject responseJson, final int expectedStatus) {
        final JSONObject metaJson = responseJson.getJSONObject(RESPONSE_META_KEY);
        final int status = metaJson.getInt("status");
        if (status != expectedStatus) {
            final @Nullable String message = getOptionalJsonValue(metaJson, "message", String.class);
            throw new OneSkyApiException(String.format(Locale.ROOT, "Expected status=%d, but API responded with status=%d, message=%s", expectedStatus, status, message));
        }
    }

    private static <T> List<T> getResponseDataList(final JSONArray dataJson, final Function<JSONObject, T> dataItemConverter) {
        return StreamSupport.stream(dataJson.spliterator(), false)
            .map(dataItem -> {
                if (!(dataItem instanceof JSONObject)) {
                    throw unexpectedJsonTypeException(RESPONSE_DATA_KEY + "[].*", dataItem, JSONObject.class);
                }
                return dataItemConverter.apply((JSONObject) dataItem);
            })
            .collect(toUnmodifiableList());
    }

}
