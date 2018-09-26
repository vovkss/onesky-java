package info.datamuse.onesky;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OneSkyFilesApiTest extends AbstractOneSkyApiTest {

    @Test
    public void testCRUDProject() throws InterruptedException {
        // Test Data Begin {{{

        final List<OneSkyProjectTypesApi.ProjectType> projectTypes = getOneSkyClient().projectTypes().list().join();

        final OneSkyProjectTypesApi.ProjectType projectType = projectTypes.get(0);
        final String projectGroupName = "TestProjectGroup-" + Instant.now().getEpochSecond();
        final String projectName = "TestProject-" + Instant.now().getEpochSecond();
        final String projectDesc = projectName + "-desc";

        final var oneSkyClient = getOneSkyClient();

        // Create
        final OneSkyProjectGroupsApi.ProjectGroup projectGroup = oneSkyClient.projectGroups().create(projectGroupName, Locale.GERMAN).join();
        final OneSkyProjectsApi.Project project = oneSkyClient.projects().create(projectGroup.getId(), projectType.getCode(), projectName, projectDesc).join();

        final String fileName = "fruits.yaml";
        final String fileContent = "fruits:\n" +
                "    - Apple\n" +
                "    - Orange\n" +
                "    - Strawberry\n" +
                "    - Mango";
        final OneSkyFilesApi.FileFormat fileFormat = OneSkyFilesApi.FileFormat.YAML;
        final OneSkyFilesApi.File projectFile = oneSkyClient.files().upload(project.getId(), fileFormat, fileName,
                new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8)), Locale.GERMAN
        ).join();
        assertThat(projectFile.getName(), is(equalTo(fileName)));
        assertThat(projectFile.getLocale(), is(equalTo(Locale.GERMAN)));
        assertThat(projectFile.getFormat(), is(equalTo(fileFormat)));
        assertThat(projectFile.getImportStatus(), is(notNullValue()));
        assertThat(projectFile.getImportStatus().getLastImportedAt(), is(notNullValue()));

        //needed for import completion
        Thread.sleep(5000);

        // Retrieve list
        final Page<OneSkyFilesApi.File> projectFilesPage = oneSkyClient.files().pagedList(project.getId(), 1L, 80L).join();
        assertThat(projectFilesPage.getPageItems(), is(not(empty())));
        assertThat(projectFilesPage.getPageNumber(), is(equalTo(1L)));
        assertThat(projectFilesPage.getMaxItemsPerPage(), is(equalTo(80L)));
        assertThat(projectFilesPage.getTotalItemsCount(), is(greaterThanOrEqualTo(1L)));
        assertThat(projectFilesPage.getTotalPagesCount(), is(greaterThanOrEqualTo(1L)));

        final OneSkyFilesApi.File projectFileItem = projectFilesPage.getPageItems().get(0);
        assertThat(projectFileItem.getName(), is(equalTo(fileName)));
        assertThat(projectFileItem.getCountOfStrings(), is(equalTo(4)));
        assertThat(projectFileItem.getImportStatus(), is(notNullValue()));
        assertThat(projectFileItem.getImportStatus().getStatus(), is(notNullValue()));

        // Delete
        oneSkyClient.files().delete(project.getId(), fileName).join();

        // Test retrieving or deleting non-existing items
        final Page<OneSkyFilesApi.File> emptyProjectFilesPage = oneSkyClient.files().pagedList(project.getId(), 1L, 80L).join();
        assertThat(emptyProjectFilesPage.getPageItems(), is(empty()));

        // Delete
        oneSkyClient.projectGroups().delete(projectGroup.getId()).join();

        // }}} Test Data End
    }
}
