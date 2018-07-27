package info.datamuse.onesky;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static info.datamuse.onesky.internal.ListUtils.listRequireNonNullItems;

/**
 * Represents the result of a paginated list query.
 *
 * @param <T> list item type
 */
public final class Page<T> {

    private final List<T> pageItems;
    private final long pageNumber;
    private final long maxItemsPerPage;
    private final long totalItemsCount;
    private final long totalPagesCount;

    /**
     * Page constructor.
     *
     * @param pageItems items on the current page
     * @param pageNumber current page number ({@code 1}-based)
     * @param maxItemsPerPage maximum number of items per page ("page size")
     * @param totalItemsCount total number of items (on all pages)
     * @param totalPagesCount total number of pages
     */
    public Page(
        final List<T> pageItems,
        final long pageNumber,
        final long maxItemsPerPage,
        final long totalItemsCount,
        final long totalPagesCount
    ) {
        this.pageItems = listRequireNonNullItems(pageItems);
        this.pageNumber = pageNumber;
        this.maxItemsPerPage = maxItemsPerPage;
        this.totalItemsCount = totalItemsCount;
        this.totalPagesCount = totalPagesCount;
    }

    /**
     * Returns list of items on the current page.
     *
     * @return items on the current page
     */
    public List<T> getPageItems() {
        return pageItems;
    }

    /**
     * Returns current page number ({@code 1}-based).
     *
     * @return current page number
     */
    public long getPageNumber() {
        return pageNumber;
    }

    /**
     * Returns maximum number of items per page ("page size").
     *
     * @return page size
     */
    public long getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    /**
     * Returns total number of items (on all pages).
     *
     * @return total number of items
     */
    public long getTotalItemsCount() {
        return totalItemsCount;
    }

    /**
     * Returns total number of pages.
     *
     * @return total number of pages
     */
    public long getTotalPagesCount() {
        return totalPagesCount;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Page)) {
            return false;
        }
        final Page<?> theObj = (Page<?>) obj;
        return
            pageItems.equals(theObj.pageItems)
            && pageNumber == theObj.pageNumber
            && maxItemsPerPage == theObj.maxItemsPerPage
            && totalItemsCount == theObj.totalItemsCount
            && totalPagesCount == theObj.totalPagesCount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pageItems, pageNumber, maxItemsPerPage, totalItemsCount);
    }

    @Override
    public String toString() {
        return String.format(
            Locale.ROOT,
            "Page{pageItems=%s, pageNumber=%d, maxItemsPerPage=%d, totalItemsCount=%d, totalPagesCount=%d}",
            pageItems, pageNumber, maxItemsPerPage, totalItemsCount, totalPagesCount
        );
    }

}
