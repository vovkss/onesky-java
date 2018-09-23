package info.datamuse.onesky;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class OneSkyProjectsApiTest extends AbstractOneSkyApiTest {

    @Test
    public void testCRUDProject() {
        // Test Data Begin {{{
        final List<OneSkyProjectTypesApi.ProjectType> projectTypes = getOneSkyClient().projectTypes().list().join();

        assertThat(projectTypes, hasSize(greaterThan(1)));

        final OneSkyProjectTypesApi.ProjectType projectType1 = projectTypes.get(0);
        final OneSkyProjectTypesApi.ProjectType projectType2 = projectTypes.get(1);

        final String projectGroupName = "TestProjectGroup-" + Instant.now().getEpochSecond();

        final String projectNameBase = "TestProject-" + Instant.now().getEpochSecond() + "-";
        final String projectName1 = projectNameBase + "1";
        final String projectName2 = projectNameBase + "2";
        final String projectDesc1 = projectName1 + "-desc";
        final String projectDesc2 = projectName2 + "-desc";

        final var oneSkyClient = getOneSkyClient();

        // Create
        final OneSkyProjectGroupsApi.ProjectGroup projectGroup = oneSkyClient.projectGroups().create(projectGroupName, Locale.GERMAN).join();

        final OneSkyProjectsApi.Project project1 = oneSkyClient.projects().create(projectGroup.getId(), projectType1.getCode(), projectName1, projectDesc1).join();
        final OneSkyProjectsApi.Project project2 = oneSkyClient.projects().create(projectGroup.getId(), projectType2.getCode(), projectName2, projectDesc2).join();

        assertThat(project1.getName(), is(equalTo(projectName1)));
        assertThat(project1.getDescription(), is(equalTo(projectDesc1)));
        assertThat(project1.getProjectType(), is(equalTo(projectType1)));

        assertThat(project2.getName(), is(equalTo(projectName2)));
        assertThat(project2.getDescription(), is(equalTo(projectDesc2)));
        assertThat(project2.getProjectType(), is(equalTo(projectType2)));

        final long projectId1 = project1.getId();

        // Retrieve list
        final List<OneSkyProjectsApi.Project> projects = oneSkyClient.projects().list(projectGroup.getId()).join();
        assertThat(projects, hasSize(equalTo(2)));
        assertThat(projects, hasItem(is(new OneSkyProjectsApi.Project(projectId1, projectName1, null, null, null, null))));
        assertThat(projects, hasItem(is(new OneSkyProjectsApi.Project(project2.getId(), projectName2, null, null, null, null))));

        // Retrieve single element
        final OneSkyProjectsApi.Project retrievedProject1 = oneSkyClient.projects().retrieve(projectId1).join();
        assertThat(
                retrievedProject1,
                is(equalTo(
                        new OneSkyProjectsApi.Project(projectId1, projectName1, projectDesc1, projectType1, 0, 0)
                ))
        );

        // Update
        oneSkyClient.projects().update(projectId1, "name1", "desc1").join();
        final OneSkyProjectsApi.Project retrievedProjectUpdated = oneSkyClient.projects().retrieve(projectId1).join();
        assertThat(
                retrievedProjectUpdated,
                is(equalTo(
                        new OneSkyProjectsApi.Project(projectId1, "name1", "desc1", projectType1, 0, 0)
                ))
        );

        // Delete
        oneSkyClient.projects().delete(project1.getId()).join();
        oneSkyClient.projects().delete(project2.getId()).join();

        // Test retrieving or deleting non-existing items
        assertThrows(
                CompletionException.class,
                () -> oneSkyClient.projects().retrieve(projectId1).join()
        );

        // Delete
        oneSkyClient.projectGroups().delete(projectGroup.getId()).join();

        // }}} Test Data End
    }
}