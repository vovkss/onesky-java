package info.datamuse.onesky;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;

public final class PageTest {

    @Test
    public void testPageDataClass() {
        // Test Data Begin {{{
        final var page = new Page<>(
            List.of("magenta", "brown", "gray", "dark gray", "bright blue"),
            2L,
            5L,
            16L,
            4L
        );

        assertThat(page.getPageItems(), is(equalTo(List.of("magenta", "brown", "gray", "dark gray", "bright blue"))));
        assertThat(page.getPageNumber(), is(equalTo(2L)));
        assertThat(page.getMaxItemsPerPage(), is(equalTo(5L)));
        assertThat(page.getTotalItemsCount(), is(equalTo(16L)));
        assertThat(page.getTotalPagesCount(), is(equalTo(4L)));

        new EqualsTester()
            .addEqualityGroup(
                page,
                page
            )
            .addEqualityGroup(
                new Page<>(List.of("magenta", "brown", "GREY", "dark gray", "bright blue"), 2L, 5L, 16L, 4L)
            )
            .addEqualityGroup(
                new Page<>(List.of("magenta", "brown", "gray", "dark gray", "bright blue"), 3L, 5L, 16L, 4L)
            )
            .addEqualityGroup(
                new Page<>(List.of("magenta", "brown", "gray", "dark gray", "bright blue"), 2L, 10L, 16L, 4L)
            )
            .addEqualityGroup(
                new Page<>(List.of("magenta", "brown", "gray", "dark gray", "bright blue"), 2L, 5L, 18L, 4L)
            )
            .addEqualityGroup(
                new Page<>(List.of("magenta", "brown", "gray", "dark gray", "bright blue"), 2L, 5L, 16L, 5L)
            )
            .testEquals();

        assertThat(
            page,
            hasToString(
                "Page{pageItems=[magenta, brown, gray, dark gray, bright blue], pageNumber=2, maxItemsPerPage=5, totalItemsCount=16, totalPagesCount=4}"
            )
        );
        // }}} Test Data End
    }

}
