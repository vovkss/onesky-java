package info.datamuse.onesky;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;

public final class OneSkyProjectTypesApiTest extends AbstractOneSkyApiTest {

    @Test
    public void testProjectTypeDataClass() {
        // Test Data Begin {{{
        final var windowsMetroAppProjectType = new OneSkyProjectTypesApi.ProjectType("windows-metro", "Windows Metro App");

        assertThat(windowsMetroAppProjectType.getCode(), is(equalTo("windows-metro")));
        assertThat(windowsMetroAppProjectType.getName(), is(equalTo("Windows Metro App")));

        new EqualsTester()
            .addEqualityGroup(
                windowsMetroAppProjectType,
                windowsMetroAppProjectType,
                new OneSkyProjectTypesApi.ProjectType("windows-metro", "Windows Metro App")
            )
            .addEqualityGroup(
                new OneSkyProjectTypesApi.ProjectType("windows-metro", "Windows Metro")
            )
            .addEqualityGroup(
                new OneSkyProjectTypesApi.ProjectType("windows-metro-app", "Windows Metro App")
            )
            .addEqualityGroup(
                new OneSkyProjectTypesApi.ProjectType("webapp-others", "Others")
            )
            .testEquals();

        assertThat(windowsMetroAppProjectType, hasToString("OneSkyProjectTypesApi.ProjectType{code=windows-metro, name=Windows Metro App}"));
        // }}} Test Data End
    }

    @Test
    public void testListProjectTypes() {
        // Test Data Begin {{{
        final List<OneSkyProjectTypesApi.ProjectType> projectTypes = getOneSkyClient().projectTypes().list().join();
        assertThat(projectTypes, hasItems(
            new OneSkyProjectTypesApi.ProjectType("ios", "iPhone/iPad App"),
            new OneSkyProjectTypesApi.ProjectType("android", "Android App"),
            new OneSkyProjectTypesApi.ProjectType("website", "Regular Website")
        ));
        // }}} Test Data End
    }

}
