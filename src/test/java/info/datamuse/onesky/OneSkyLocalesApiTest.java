package info.datamuse.onesky;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

public final class OneSkyLocalesApiTest extends AbstractOneSkyApiTest {

    @Test
    public void testListLocales() {
        // Test Data Begin {{{
        final List<Locale> oneSkyLocales = getOneSkyClient().locales().list().join();
        assertThat(oneSkyLocales, hasSize(greaterThanOrEqualTo(50)));
        assertThat(oneSkyLocales, hasItems(
            Locale.GERMANY,
            Locale.US,
            new Locale("ru", "RU"),
            new Locale("uk"),
            new Locale("zh", "CN")
        ));
        // }}} Test Data End
    }

}
