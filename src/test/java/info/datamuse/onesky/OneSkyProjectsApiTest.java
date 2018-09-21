package info.datamuse.onesky;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Locale;

class OneSkyProjectsApiTest extends AbstractOneSkyApiTest {

    @Test
    public void testCRUDProject() {
        // Test Data Begin {{{

        final String projectGroupNameBase = "TestProjectGroup-" + Instant.now().getEpochSecond() + "-";
        final String projectGroupName1 = projectGroupNameBase + "1";

        final var oneSkyClient = getOneSkyClient();

        // Create
        final OneSkyProjectGroupsApi.ProjectGroup projectGroup1 = oneSkyClient.projectGroups().create(projectGroupName1, Locale.GERMAN).join();

        // Delete
        oneSkyClient.projectGroups().delete(projectGroup1.getId()).join();

        // }}} Test Data End
    }
}