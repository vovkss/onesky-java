package info.datamuse.onesky;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;

public final class OneSkyProjectGroupsApiTest extends AbstractOneSkyApiTest {

    @Test
    public void testProjectGroupDataClass() {
        // Test Data Begin {{{
        final var cologneProjectGroup = new OneSkyProjectGroupsApi.ProjectGroup(
            4711L,
            "Cologne",
            Locale.GERMAN,
            List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE),
            42
        );

        assertThat(cologneProjectGroup.getId(), is(equalTo(4711L)));
        assertThat(cologneProjectGroup.getName(), is(equalTo("Cologne")));
        assertThat(cologneProjectGroup.getBaseLocale(), is(equalTo(Locale.GERMAN)));
        assertThat(cologneProjectGroup.getEnabledLocales(), is(equalTo(List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE))));
        assertThat(cologneProjectGroup.getProjectCount(), is(equalTo(42)));

        new EqualsTester()
            .addEqualityGroup(
                cologneProjectGroup,
                cologneProjectGroup
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(1147L, "Cologne", Locale.GERMAN, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 42)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Koeln", Locale.GERMAN, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 42)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", null, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 42)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.FRENCH, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 42)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.GERMAN, null, 42)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.GERMAN, List.of(Locale.SIMPLIFIED_CHINESE, Locale.CHINESE), 42)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.GERMAN, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), null)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.GERMAN, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 24)
            )
            .testEquals();

        assertThat(cologneProjectGroup, hasToString("OneSkyProjectGroupsApi.ProjectGroup{id=4711, name=Cologne}"));
        // }}} Test Data End
    }

    @Test
    public void testCRUDProjectGroup() {
        // Test Data Begin {{{
        final String projectGroupNameBase = "TestProjectGroup-" + Instant.now().getEpochSecond() + "-";
        final String projectGroupName1 = projectGroupNameBase + "1";
        final String projectGroupName2 = projectGroupNameBase + "2";
        final String projectGroupName3 = projectGroupNameBase + "3";

        final var oneSkyClient = getOneSkyClient();

        // Create
        final OneSkyProjectGroupsApi.ProjectGroup projectGroup1 = oneSkyClient.projectGroups().create(projectGroupName1, Locale.GERMAN).join();
        final OneSkyProjectGroupsApi.ProjectGroup projectGroup2 = oneSkyClient.projectGroups().create(projectGroupName2, Locale.FRENCH).join();
        final OneSkyProjectGroupsApi.ProjectGroup projectGroup3 = oneSkyClient.projectGroups().create(projectGroupName3, null).join();

        assertThat(projectGroup1.getName(), is(equalTo(projectGroupName1)));
        assertThat(projectGroup1.getBaseLocale(), is(equalTo(Locale.GERMAN)));

        assertThat(projectGroup2.getName(), is(equalTo(projectGroupName2)));
        assertThat(projectGroup2.getBaseLocale(), is(equalTo(Locale.FRENCH)));

        assertThat(projectGroup3.getName(), is(equalTo(projectGroupName3)));
        assertThat(projectGroup3.getBaseLocale(), is(equalTo(Locale.ENGLISH)));
        // }}} Test Data End
    }

}
