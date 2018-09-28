package info.datamuse.onesky;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class OneSkyFilesApiTest extends AbstractOneSkyApiTest {

    @Test
    public void testCRUDProject() throws InterruptedException, IOException {
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


        final Path path = pathToResourceFile("fruits.yaml");
        final String fileName = path.getFileName().toString();
        final OneSkyFilesApi.FileFormat fileFormat = OneSkyFilesApi.FileFormat.YAML;

        final OneSkyFilesApi.File projectFile = oneSkyClient.files().upload(project.getId(), fileFormat, path, Locale.GERMAN).join();
        assertThat(projectFile.getName(), is(equalTo(fileName)));
        assertThat(projectFile.getLocale(), is(equalTo(Locale.GERMAN)));
        assertThat(projectFile.getFormat(), is(equalTo(fileFormat)));
        assertThat(projectFile.getImportStatus(), is(notNullValue()));
        assertThat(projectFile.getImportStatus().getLastImportedAt(), is(notNullValue()));

        //needed for import completion
        Thread.sleep(5000);

        // Retrieve list of import tasks
        final Page<OneSkyImportTasksApi.ImportTask> importTasksPage = oneSkyClient.importTasks().pagedList(project.getId(), OneSkyFilesApi.FileStatus.ALL, 1L, 80L).join();
        assertThat(importTasksPage.getPageItems(), is(not(empty())));
        assertThat(importTasksPage.getPageNumber(), is(equalTo(1L)));
        assertThat(importTasksPage.getMaxItemsPerPage(), is(equalTo(80L)));
        assertThat(importTasksPage.getTotalItemsCount(), is(greaterThanOrEqualTo(1L)));
        assertThat(importTasksPage.getTotalPagesCount(), is(greaterThanOrEqualTo(1L)));

        final OneSkyImportTasksApi.ImportTask importTaskItem = importTasksPage.getPageItems().get(0);
        assertThat(importTaskItem.getId(), is(equalTo(projectFile.getImportStatus().getId())));
        assertThat(importTaskItem.getFileName(), is(equalTo(fileName)));
        assertThat(importTaskItem.getImportStatus(), is(notNullValue()));
        assertThat(importTaskItem.getImportStatus().getStatus(), is(notNullValue()));
        assertThat(importTaskItem.getImportStatus().getLastImportedAt(), is(notNullValue()));

        // Retrieve import task
        final OneSkyImportTasksApi.ImportTask importTask = oneSkyClient.importTasks().retrieve(project.getId(), importTaskItem.getId()).join();
        assertThat(importTask.getId(), is(equalTo(projectFile.getImportStatus().getId())));
        assertThat(importTask.getLocale(), is(equalTo(Locale.GERMAN)));
        assertThat(importTask.getFormat(), is(equalTo(fileFormat)));
        assertThat(importTask.getImportStatus(), is(notNullValue()));
        assertThat(importTask.getImportStatus().getLastImportedAt(), is(notNullValue()));

        // Retrieve list
        final Page<OneSkyFilesApi.File> projectFilesPage = oneSkyClient.files().pagedList(project.getId(), 1L, 80L).join();
        assertThat(projectFilesPage.getPageItems(), is(not(empty())));
        assertThat(projectFilesPage.getPageNumber(), is(equalTo(1L)));
        assertThat(projectFilesPage.getMaxItemsPerPage(), is(equalTo(80L)));
        assertThat(projectFilesPage.getTotalItemsCount(), is(greaterThanOrEqualTo(1L)));
        assertThat(projectFilesPage.getTotalPagesCount(), is(greaterThanOrEqualTo(1L)));

        final OneSkyFilesApi.File projectFileItem = projectFilesPage.getPageItems().get(0);
        assertThat(projectFileItem.getName(), is(equalTo(fileName)));
        assertThat(projectFileItem.getCountOfStrings(), is(notNullValue()));
        assertThat(projectFileItem.getImportStatus(), is(notNullValue()));
        assertThat(projectFileItem.getImportStatus().getStatus(), is(notNullValue()));

        // Translations
        final var translationStream = oneSkyClient.translations().export(project.getId(), Locale.GERMAN, fileName).join();
        System.out.println(new BufferedReader(new InputStreamReader(translationStream)).lines().collect(Collectors.joining("\n")));
        System.out.println(new BufferedReader(new InputStreamReader(Files.newInputStream(path))).lines().collect(Collectors.joining("\n")));

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
