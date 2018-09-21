package info.datamuse.onesky;

import info.datamuse.onesky.OneSkyProjectTypesApi.ProjectType;
import info.datamuse.onesky.internal.AbstractOneSkyApi;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.net.http.HttpClient;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static info.datamuse.onesky.OneSkyProjectGroupsApi.PROJECT_GROUP_BY_ID_API_URL_TEMPLATE;
import static info.datamuse.onesky.OneSkyProjectTypesApi.PROJECT_TYPE_CODE_KEY;
import static info.datamuse.onesky.OneSkyProjectTypesApi.PROJECT_TYPE_NAME_KEY;
import static info.datamuse.onesky.internal.JsonUtils.getOptionalJsonValue;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

public final class OneSkyProjectsApi extends AbstractOneSkyApi {

    OneSkyProjectsApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    public static final class Project {
        private final long id;
        private final String name;
        private final @Nullable String description;
        private final @Nullable ProjectType projectType;
        private final @Nullable Long countOfStrings;
        private final @Nullable Long countOfWords;

        public Project(
                final long id,
                final String name,
                final @Nullable String description,
                final @Nullable ProjectType projectType,
                final @Nullable Long countOfStrings,
                final @Nullable Long countOfWords) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.projectType = projectType;
            this.countOfStrings = countOfStrings;
            this.countOfWords = countOfWords;
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Nullable
        public String getDescription() {
            return description;
        }

        @Nullable
        public ProjectType getProjectType() {
            return projectType;
        }

        @Nullable
        public Long getCountOfStrings() {
            return countOfStrings;
        }

        @Nullable
        public Long getCountOfWords() {
            return countOfWords;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Project project = (Project) o;
            return id == project.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return "Project{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", projectType=" + projectType +
                    ", countOfStrings=" + countOfStrings +
                    ", countOfWords=" + countOfWords +
                    '}';
        }
    }

    private static final String PROJECTS_API_URL = API_BASE_URL + "/projects";
    private static final String PROJECTS_BY_ID_API_URL = PROJECTS_API_URL + "/%d";
    private static final String PROJECTS_BY_GROUP_ID_API_URL_TEMPLATE = PROJECT_GROUP_BY_ID_API_URL_TEMPLATE + "/projects";

    private static final String PROJECT_ID_KEY = "id";
    private static final String PROJECT_NAME_KEY = "name";
    private static final String PROJECT_DESCRIPTION_KEY = "description";
    private static final String PROJECT_PROJECT_TYPE_KEY = "project_type";
    private static final String PROJECT_STRING_COUNT_KEY = "string_count";
    private static final String PROJECT_WORD_COUNT_KEY = "word_count";

    private static final String PROJECT_PROJECT_TYPE_PARAM = PROJECT_PROJECT_TYPE_KEY;
    private static final String PROJECT_NAME_PARAM = PROJECT_NAME_KEY;
    private static final String PROJECT_DESCRIPTION_PARAM = PROJECT_DESCRIPTION_KEY;

    public CompletableFuture<Project> create(final long projectGroupId, final String type, final @Nullable String name, final @Nullable String description) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(PROJECT_PROJECT_TYPE_PARAM, requireNonNull(type));
        if (name != null) {
            parameters.put(PROJECT_NAME_PARAM, name);
        }
        if (description != null) {
            parameters.put(PROJECT_DESCRIPTION_PARAM, description);
        }

        return apiCreateRequest(
                String.format(Locale.ROOT, PROJECTS_BY_GROUP_ID_API_URL_TEMPLATE, projectGroupId),
                parameters,
                data -> toProject(data)
        );
    }

    public CompletableFuture<List<Project>> list(final long projectGroupId) {
        return apiGetListRequest(
                String.format(Locale.ROOT, PROJECTS_BY_GROUP_ID_API_URL_TEMPLATE, projectGroupId),
                emptyMap(),
                data -> toProject(data)
        );
    }

    public CompletableFuture<Project> retrieve(final long projectId) {
        return apiGetObjectRequest(
                String.format(Locale.ROOT, PROJECTS_BY_ID_API_URL, projectId),
                emptyMap(),
                data -> toProject(data)
        );
    }

    private static Project toProject(final JSONObject projectJson) {
        @Nullable ProjectType projectType = getOptionalJsonValue(projectJson, PROJECT_PROJECT_TYPE_KEY, JSONObject.class, jsonProjectType -> new ProjectType(
                jsonProjectType.getString(PROJECT_TYPE_CODE_KEY),
                jsonProjectType.getString(PROJECT_TYPE_NAME_KEY)));
        return new Project(
                projectJson.getLong(PROJECT_ID_KEY),
                projectJson.getString(PROJECT_NAME_KEY),
                getOptionalJsonValue(projectJson, PROJECT_DESCRIPTION_KEY, String.class, identity()),
                projectType,
                getOptionalJsonValue(projectJson, PROJECT_STRING_COUNT_KEY, String.class, Long::parseLong),
                getOptionalJsonValue(projectJson, PROJECT_WORD_COUNT_KEY, String.class, Long::parseLong)
        );
    }

}
