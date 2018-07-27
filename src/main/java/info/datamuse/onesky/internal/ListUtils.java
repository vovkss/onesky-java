package info.datamuse.onesky.internal;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

/**
 * {@link List} utilities.
 */
public final class ListUtils {

    private ListUtils() {
        // Namespace
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

}
