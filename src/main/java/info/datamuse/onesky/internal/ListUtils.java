package info.datamuse.onesky.internal;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * {@link List} utilities.
 */
public final class ListUtils {

    private ListUtils() {
        // Namespace
    }

    /**
     * Checks that the list and the list items are non-{@code null} and returns the {@code list}.
     *
     * @param <T> list item type
     * @param list the list to check
     * @return {@code list}
     */
    public static <T> List<T> listRequireNonNullItems(final List<T> list) {
        return requireNonNull(optionalListRequireNonNullItems(list));
    }

    /**
     * If {@code list} is non-{@code null}, checks that the list items are non-{@code null} and returns the {@code list};
     * otherwise returns {@code null}.
     *
     * @param <T> list item type
     * @param list optional list
     * @return {@code list}
     */
    public static @Nullable <T> List<T> optionalListRequireNonNullItems(final @Nullable List<T> list) {
        if (list != null) {
            list.forEach(Objects::requireNonNull);
        }
        return list;
    }

    public static <T> List<T> listFromMapEntries(final Map<T, T> map) {
        final List<T> list = new ArrayList<>();
        for (Map.Entry<T, T> entry : map.entrySet()) {
            list.add(entry.getKey());
            list.add(entry.getValue());
        }
        return list;
    }

}
