package info.datamuse.onesky.internal;

import org.json.JSONObject;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Function;

/**
 * JSON processing utilities.
 */
public final class JsonUtils {

    private JsonUtils() {
        // Namespace
    }

    /**
     * Returns JSON object's property value with the given key if it exists, or {@code null} otherwise.
     *
     * @param <T> JSON value type
     * @param json JSON object
     * @param key value key
     * @param jsonValueClass JSON value class
     * @return property value for the specified key if it exists, else {@code null}
     */
    public static @Nullable <T> T getOptionalJsonValue(final JSONObject json, final String key, final Class<T> jsonValueClass) {
        if (json.has(key)) {
            final Object value = json.get(key);
            if (jsonValueClass.isInstance(value)) {
                return (T) value;
            } else {
                throw unexpectedJsonTypeException(key, json, jsonValueClass);
            }
        } else {
            return null;
        }
    }

    /**
     * Applies a converter function to the JSON object's property value with the given key if it exists,
     * or returns {@code null} otherwise.
     *
     * @param <T> JSON value type
     * @param <R> conversion result type
     * @param json JSON object
     * @param key value key
     * @param jsonValueClass JSON value class
     * @return converted property value for the specified key if it exists, else {@code null}
     */
    public static @Nullable <T, R> R getOptionalJsonValue(final JSONObject json, final String key, final Class<T> jsonValueClass, final Function<T, R> valueConverter) {
        final @Nullable T jsonValue = getOptionalJsonValue(json, key, jsonValueClass);
        return jsonValue != null ? valueConverter.apply(jsonValue) : null;
    }

    /**
     * Returns an exception to be thrown if a JSON value has invalid type.
     *
     * @param name JSON key name
     * @param json JSON value
     * @param expectedClass expected JSON value class
     * @return exception which represents an invalid JSON value type
     */
    public static IllegalArgumentException unexpectedJsonTypeException(final String name, final @Nullable Object json, final Class<?> expectedClass) {
        return new IllegalArgumentException(String.format(
            Locale.ROOT,
            "`%s` was expected to be of type `%s`, but was of type `%s`",
            name, expectedClass.getSimpleName(), getTypeName(json)
        ));
    }
    private static String getTypeName(final @Nullable Object obj) {
        return obj != null ? obj.getClass().getSimpleName() : "null";
    }

}
