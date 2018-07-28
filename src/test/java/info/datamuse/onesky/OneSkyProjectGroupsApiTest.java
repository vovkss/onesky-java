package info.datamuse.onesky;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class OneSkyProjectGroupsApiTest extends AbstractOneSkyApiTest {

    @Test
    public void testProjectGroupDataClass() {
        // Test Data Begin {{{
        final var cologneProjectGroup = new OneSkyProjectGroupsApi.ProjectGroup(
            4711L,
            "Cologne",
            Locale.GERMAN,
            List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE),
            42L
        );

        assertThat(cologneProjectGroup.getId(), is(equalTo(4711L)));
        assertThat(cologneProjectGroup.getName(), is(equalTo("Cologne")));
        assertThat(cologneProjectGroup.getBaseLocale(), is(equalTo(Locale.GERMAN)));
        assertThat(cologneProjectGroup.getEnabledLocales(), is(equalTo(List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE))));
        assertThat(cologneProjectGroup.getProjectCount(), is(equalTo(42L)));

        new EqualsTester()
            .addEqualityGroup(
                cologneProjectGroup,
                cologneProjectGroup
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(1147L, "Cologne", Locale.GERMAN, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 42L)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Koeln", Locale.GERMAN, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 42L)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", null, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 42L)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.FRENCH, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 42L)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.GERMAN, null, 42L)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.GERMAN, List.of(Locale.SIMPLIFIED_CHINESE, Locale.CHINESE), 42L)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.GERMAN, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), null)
            )
            .addEqualityGroup(
                new OneSkyProjectGroupsApi.ProjectGroup(4711L, "Cologne", Locale.GERMAN, List.of(Locale.CHINESE, Locale.SIMPLIFIED_CHINESE), 24L)
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

        // Retrieve list
        final Page<OneSkyProjectGroupsApi.ProjectGroup> projectGroupsPage1 = oneSkyClient.projectGroups().pagedList(1L, 80L).join();
        assertThat(projectGroupsPage1.getPageItems(), is(not(empty())));
        assertThat(projectGroupsPage1.getPageNumber(), is(equalTo(1L)));
        assertThat(projectGroupsPage1.getMaxItemsPerPage(), is(equalTo(80L)));
        assertThat(projectGroupsPage1.getTotalItemsCount(), is(greaterThanOrEqualTo(3L)));
        assertThat(projectGroupsPage1.getTotalPagesCount(), is(greaterThanOrEqualTo(1L)));

        // Retrieve single element
        final long projectGroupId1 = projectGroup1.getId();
        final OneSkyProjectGroupsApi.ProjectGroup retrievedProjectGroup1 = oneSkyClient.projectGroups().retrieve(projectGroupId1).join();
        assertThat(
            retrievedProjectGroup1,
            is(equalTo(
                new OneSkyProjectGroupsApi.ProjectGroup(projectGroupId1, projectGroupName1, Locale.GERMAN, List.of(Locale.GERMAN), 0L)
            ))
        );

        // Delete
        oneSkyClient.projectGroups().delete(projectGroupId1).join();
        oneSkyClient.projectGroups().delete(projectGroup2.getId()).join();
        oneSkyClient.projectGroups().delete(projectGroup3.getId()).join();

        // Test retrieving or deleting non-existing items
        assertThrows(
            CompletionException.class,
            () -> oneSkyClient.projectGroups().retrieve(projectGroupId1).join()
        );
        assertThrows(
            CompletionException.class,
            () -> oneSkyClient.projectGroups().delete(projectGroupId1).join()
        );
        // }}} Test Data End
    }

}
