package info.datamuse.onesky.internal;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

/**
 * Abstract base class for OneSky API Wrappers.
 */
public abstract class AbstractOneSkyApi {

    /**
     * OneSky API base URL
     */
    public static final String API_BASE_URL = "https://platform.api.onesky.io/1";

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

    protected final <T> CompletableFuture<List<T>> apiGetListOfObjectsRequest(
        final String apiUrl,
        final Function<JSONObject, T> dataItemsConverter
    ) {
        return apiGetListOfObjectsRequest(apiUrl, emptyMap(), dataItemsConverter);
    }

    protected final <T> CompletableFuture<List<T>> apiGetListOfObjectsRequest(
        final String apiUrl,
        final Map<String, String> parameters,
        final Function<JSONObject, T> dataItemsConverter
    ) {
        return
            apiGetRequest(apiUrl, parameters)
                .thenApply(/* @Nullable */ data -> {
                    if (!(data instanceof JSONArray)) {
                        throw unexpectedResponseDataTypeException(data, JSONArray.class, apiUrl);
                    }
                    return StreamSupport.stream(((JSONArray) data).spliterator(), false)
                        .map(dataItem -> {
                            if (!(dataItem instanceof JSONObject)) {
                                throw new IllegalArgumentException(String.format(
                                    Locale.ROOT,
                                    "OneSky API response data was expected to be an array of objects, but an item of type `%s` was received. API URL: `%s`",
                                    (data != null ? data.getClass().getSimpleName() : "null"),
                                    apiUrl
                                ));
                            }
                            return dataItemsConverter.apply((JSONObject) dataItem);
                        })
                        .collect(toUnmodifiableList());
                    }
                );
    }

    protected final CompletableFuture<?> apiGetRequest(final String apiUrl, final Map<String, String> parameters) {
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
                .GET()
                .header("Content-Type", "application/json")
                .build(); // TODO: cache this
        return
            httpClient
                .sendAsync(apiHttpRequest, HttpResponse.BodyHandlers.ofString())
                .thenApply(httpResponse -> {
                    final JSONObject response = new JSONObject(httpResponse.body()); // TODO: handle JSON parsing problems
                    final @Nullable JSONObject meta = response.getJSONObject("meta");
                    // TODO: check that meta, data exist, check status, etc.
                    final @Nullable Long status = meta.getLong("status");
                    if (status != 200) {
                        throw new RuntimeException(Long.toString(status)); // TODO: use proper exception
                    }
                    return response.get("data");
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

    private static IllegalArgumentException unexpectedResponseDataTypeException(final @Nullable Object data, final Class<?> expectedClass, final String apiUrl) {
        return new IllegalArgumentException(String.format(
            Locale.ROOT,
            "OneSky API response data was expected to be of type `%s`, but was of type `%s`. API URL: `%s`",
            expectedClass.getSimpleName(),
            (data != null ? data.getClass().getSimpleName() : "null"),
            apiUrl
        ));
    }

}
