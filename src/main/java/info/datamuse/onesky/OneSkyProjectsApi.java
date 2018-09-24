package info.datamuse.onesky;

import info.datamuse.onesky.OneSkyProjectTypesApi.ProjectType;
import info.datamuse.onesky.internal.AbstractOneSkyApi;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static info.datamuse.onesky.OneSkyLocalesApi.LOCALE_CODE_KEY;
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
        private final @Nullable
        String description;
        private final @Nullable
        ProjectType projectType;
        private final @Nullable
        Integer countOfStrings;
        private final @Nullable
        Integer countOfWords;
        private final @Nullable
        ProjectLanguage baseLanguage;
        private final List<ProjectLanguage> languages;

        public Project(
                final long id,
                final String name,
                final @Nullable String description,
                final @Nullable ProjectType projectType,
                final @Nullable Integer countOfStrings,
                final @Nullable Integer countOfWords,
                final @Nullable ProjectLanguage baseLanguage,
                final List<ProjectLanguage> languages) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.projectType = projectType;
            this.countOfStrings = countOfStrings;
            this.countOfWords = countOfWords;
            this.baseLanguage = baseLanguage;
            this.languages = languages;
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
        public Integer getCountOfStrings() {
            return countOfStrings;
        }

        @Nullable
        public Integer getCountOfWords() {
            return countOfWords;
        }

        @Nullable
        public ProjectLanguage getBaseLanguage() {
            return baseLanguage;
        }

        public List<ProjectLanguage> getLanguages() {
            return languages;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof Project)) return false;
            final Project project = (Project) o;
            return id == project.id &&
                    Objects.equals(name, project.name) &&
                    Objects.equals(description, project.description) &&
                    Objects.equals(projectType, project.projectType) &&
                    Objects.equals(countOfStrings, project.countOfStrings) &&
                    Objects.equals(countOfWords, project.countOfWords);
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
                    ", baseLanguage=" + baseLanguage +
                    ", languages=" + languages +
                    '}';
        }
    }

    public static final class ProjectLanguage {
        private final Locale locale;
        private final boolean isReadyToPublish;
        private final String translationProgress;
        private final Instant uploadedAt;
        private final Instant uploadedAtTimestamp;

        public ProjectLanguage(final Locale locale,
                               final boolean isReadyToPublish,
                               final String translationProgress,
                               final Instant uploadedAt,
                               final Instant uploadedAtTimestamp) {
            this.locale = locale;
            this.isReadyToPublish = isReadyToPublish;
            this.translationProgress = translationProgress;
            this.uploadedAt = uploadedAt;
            this.uploadedAtTimestamp = uploadedAtTimestamp;
        }
    }

    private static final String PROJECTS_API_URL = API_BASE_URL + "/projects";
    private static final String PROJECTS_BY_ID_API_URL_TEMPLATE = PROJECTS_API_URL + "/%d";
    private static final String PROJECT_LANGUAGES_BY_ID_API_URL_TEMPLATE = PROJECTS_BY_ID_API_URL_TEMPLATE + "/languages";
    private static final String PROJECTS_BY_GROUP_ID_API_URL_TEMPLATE = PROJECT_GROUP_BY_ID_API_URL_TEMPLATE + "/projects";

    private static final String PROJECT_ID_KEY = "id";
    private static final String PROJECT_NAME_KEY = "name";
    private static final String PROJECT_DESCRIPTION_KEY = "description";
    private static final String PROJECT_PROJECT_TYPE_KEY = "project_type";
    private static final String PROJECT_STRING_COUNT_KEY = "string_count";
    private static final String PROJECT_WORD_COUNT_KEY = "word_count";

    private static final String PROJECT_LANGUAGE_IS_BASE_LOCALE_KEY = "is_base_language";
    private static final String PROJECT_LANGUAGE_IS_READY_TO_PUBLISH_KEY = "is_ready_to_publish";
    private static final String PROJECT_LANGUAGE_TRANSLATION_PROGRESS_KEY = "translation_progress";
    private static final String PROJECT_LANGUAGE_UPLOADED_AT_KEY = "uploaded_at";
    private static final String PROJECT_LANGUAGE_UPLOADED_AT_TIMESTAMP_KEY = "uploaded_at_timestamp";

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
                data -> toProject(data, null)
        );
    }

    public CompletableFuture<List<Project>> list(final long projectGroupId) {
        return apiGetListRequest(
                String.format(Locale.ROOT, PROJECTS_BY_GROUP_ID_API_URL_TEMPLATE, projectGroupId),
                emptyMap(),
                data -> toProject(data, null)
        );
    }

    public CompletableFuture<Project> retrieve(final long projectId) {
        final CompletableFuture<JSONObject> projectJsonPromise = apiGetObjectRequest(
                String.format(Locale.ROOT, PROJECTS_BY_ID_API_URL_TEMPLATE, projectId),
                emptyMap(),
                identity()
        );
        final CompletableFuture<List<JSONObject>> projectLanguagesPromise = apiGetListRequest(
                String.format(Locale.ROOT, PROJECT_LANGUAGES_BY_ID_API_URL_TEMPLATE, projectId),
                emptyMap(),
                identity()
        );
        return projectJsonPromise.thenCombine(projectLanguagesPromise,
                (projectJson, projectLanguagesJson) -> toProject(projectJson, projectLanguagesJson));
    }

    public CompletableFuture<Void> update(final long projectId, final @Nullable String name, final @Nullable String description) {
        final Map<String, String> parameters = new HashMap<>();
        if (name != null) {
            parameters.put(PROJECT_NAME_PARAM, name);
        }
        if (description != null) {
            parameters.put(PROJECT_DESCRIPTION_PARAM, description);
        }
        return apiUpdateRequest(
                String.format(Locale.ROOT, PROJECTS_BY_ID_API_URL_TEMPLATE, projectId),
                parameters
        );
    }

    public CompletableFuture<Void> delete(final long projectId) {
        return apiDeleteRequest(
                String.format(Locale.ROOT, PROJECTS_BY_ID_API_URL_TEMPLATE, projectId),
                emptyMap()
        );
    }

    private static Project toProject(final JSONObject projectJson, final @Nullable List<JSONObject> languages) {
        @Nullable ProjectLanguage baseProjectLanguage = null;
        List<ProjectLanguage> projectLanguages = new ArrayList<>();
        if (languages != null) {
            baseProjectLanguage = languages.stream()
                    .filter(langJson -> langJson.getBoolean(PROJECT_LANGUAGE_IS_BASE_LOCALE_KEY))
                    .findFirst()
                    .map(OneSkyProjectsApi::toProjectLanguage)
                    .orElse(null);
            projectLanguages = languages.stream().map(OneSkyProjectsApi::toProjectLanguage).collect(Collectors.toUnmodifiableList());
        }

        @Nullable ProjectType projectType = getOptionalJsonValue(projectJson, PROJECT_PROJECT_TYPE_KEY, JSONObject.class, jsonProjectType -> new ProjectType(
                jsonProjectType.getString(PROJECT_TYPE_CODE_KEY),
                jsonProjectType.getString(PROJECT_TYPE_NAME_KEY)));

        return new Project(
                projectJson.getLong(PROJECT_ID_KEY),
                projectJson.getString(PROJECT_NAME_KEY),
                getOptionalJsonValue(projectJson, PROJECT_DESCRIPTION_KEY, String.class, identity()),
                projectType,
                getOptionalJsonValue(projectJson, PROJECT_STRING_COUNT_KEY, Integer.class, identity()),
                getOptionalJsonValue(projectJson, PROJECT_WORD_COUNT_KEY, Integer.class, identity()),
                baseProjectLanguage,
                projectLanguages
        );
    }

    private static ProjectLanguage toProjectLanguage(final JSONObject projectLanguageJson) {
        return new ProjectLanguage(
                Locale.forLanguageTag(projectLanguageJson.getString(LOCALE_CODE_KEY)),
                projectLanguageJson.getBoolean(PROJECT_LANGUAGE_IS_READY_TO_PUBLISH_KEY),
                projectLanguageJson.getString(PROJECT_LANGUAGE_TRANSLATION_PROGRESS_KEY),
                Instant.parse(projectLanguageJson.getString(PROJECT_LANGUAGE_UPLOADED_AT_KEY)),
                Instant.parse(projectLanguageJson.getString(PROJECT_LANGUAGE_UPLOADED_AT_TIMESTAMP_KEY))
        );
    }

}
