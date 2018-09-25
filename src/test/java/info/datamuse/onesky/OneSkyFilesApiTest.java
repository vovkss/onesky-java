package info.datamuse.onesky;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;

public class OneSkyFilesApiTest extends AbstractOneSkyApiTest {

    @Test
    public void testCRUDProject() {
        // Test Data Begin {{{

        final List<OneSkyProjectTypesApi.ProjectType> projectTypes = getOneSkyClient().projectTypes().list().join();

        assertThat(projectTypes, hasSize(greaterThan(1)));

        final OneSkyProjectTypesApi.ProjectType projectType = projectTypes.get(0);
        final String projectGroupName = "TestProjectGroup-" + Instant.now().getEpochSecond();
        final String projectName = "TestProject-" + Instant.now().getEpochSecond();
        final String projectDesc = projectName + "-desc";

        final var oneSkyClient = getOneSkyClient();

        // Create
        final OneSkyProjectGroupsApi.ProjectGroup projectGroup = oneSkyClient.projectGroups().create(projectGroupName, Locale.GERMAN).join();
        final OneSkyProjectsApi.Project project = oneSkyClient.projects().create(projectGroup.getId(), projectType.getCode(), projectName, projectDesc).join();

        final String fileContent = "word1, word2, word3";
        final String result = oneSkyClient.files().upload(project.getId(), () -> new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)), "IOS_STRINGS", null).join();
        System.out.println(result);

        // Delete
        oneSkyClient.projectGroups().delete(projectGroup.getId()).join();

        // }}} Test Data End
    }
}
