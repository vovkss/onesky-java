package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static info.datamuse.onesky.OneSkyLocalesApi.LOCALE_CODE_KEY;
import static info.datamuse.onesky.internal.JsonUtils.getOptionalJsonValue;
import static info.datamuse.onesky.internal.ListUtils.optionalListRequireNonNullItems;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toUnmodifiableList;

/**
 * OneSky Project Groups API wrapper.
 */
public final class OneSkyProjectGroupsApi extends AbstractOneSkyApi {

    /**
     * OneSky Project Group.
     */
    public static final class ProjectGroup {
        private final long id;
        private final String name;
        private final @Nullable Locale baseLocale;
        private final @Nullable List<Locale> enabledLocales;
        private final @Nullable Long projectCount;

        /**
         * Project Group constructor.
         *
         * @param id project group id
         * @param name project group name
         * @param baseLocale optional base locale
         */
        public ProjectGroup(
            final long id,
            final String name,
            final @Nullable Locale baseLocale,
            final @Nullable List<Locale> enabledLocales,
            final @Nullable Long projectCount
        ) {
            this.id = id;
            this.name = requireNonNull(name);
            this.baseLocale = baseLocale;
            this.enabledLocales = optionalListRequireNonNullItems(enabledLocales);
            this.projectCount = projectCount;
        }

        /**
         * Returns project group id.
         *
         * @return project group id
         */
        public long getId() {
            return id;
        }

        /**
         * Returns project group name.
         *
         * @return project group name
         */
        public String getName() {
            return name;
        }

        /**
         * Returns optional base locale.
         *
         * @return optional base locale
         */
        public @Nullable Locale getBaseLocale() {
            return baseLocale;
        }

        public @Nullable List<Locale> getEnabledLocales() {
            return enabledLocales;
        }

        public @Nullable Long getProjectCount() {
            return projectCount;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ProjectGroup)) {
                return false;
            }
            final ProjectGroup theObj = (ProjectGroup) obj;
            return
                id == theObj.id
                && name.equals(theObj.name)
                && Objects.equals(baseLocale, theObj.baseLocale)
                && Objects.equals(enabledLocales, theObj.enabledLocales)
                && Objects.equals(projectCount, theObj.projectCount);
        }

        @Override
        public int hashCode() {
            return Long.hashCode(id);
        }

        @Override
        public String toString() {
            return String.format(
                Locale.ROOT,
                "OneSkyProjectGroupsApi.ProjectGroup{id=%d, name=%s}",
                id, name
            );
        }
    }

    private static final String PROJECT_GROUPS_API_URL = API_BASE_URL + "/project-groups";
    private static final String PROJECT_GROUP_BY_ID_API_URL_TEMPLATE = PROJECT_GROUPS_API_URL + "/%d";
    private static final String PROJECT_GROUP_ENABLED_LOCALES_BY_ID_API_URL_TEMPLATE = PROJECT_GROUP_BY_ID_API_URL_TEMPLATE + "/languages";

    private static final String PROJECT_GROUP_NAME_PARAM = "name";
    private static final String PROJECT_GROUP_BASE_LOCALE_PARAM = "locale";

    static final String PROJECT_GROUP_ID_KEY = "id";
    static final String PROJECT_GROUP_NAME_KEY = PROJECT_GROUP_NAME_PARAM;
    static final String PROJECT_GROUP_BASE_LOCALE_KEY = "base_language";
    static final String PROJECT_GROUP_PROJECT_COUNT_KEY = "project_count";
    static final String PROJECT_GROUP_IS_BASE_LANGUAGE_KEY = "is_base_language";

    OneSkyProjectGroupsApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    /**
     * Creates a new project group.
     *
     * @param name project group name
     * @param baseLocale optional base locale
     * @return created project group ({@link CompletableFuture promise})
     */
    public CompletableFuture<ProjectGroup> create(final String name, final @Nullable Locale baseLocale) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put(PROJECT_GROUP_NAME_PARAM, requireNonNull(name));
        if (baseLocale != null) {
            parameters.put(PROJECT_GROUP_BASE_LOCALE_PARAM, baseLocale.toLanguageTag());
        }

        return apiCreateRequest(
            PROJECT_GROUPS_API_URL,
            parameters,
            data -> toProjectGroup(data, null)
        );
    }

    public CompletableFuture<Page<ProjectGroup>> pagedList(final long pageNumber, final long maxItemsPerPage) {
        return apiGetPagedListRequest(
            PROJECT_GROUPS_API_URL,
            emptyMap(),
            pageNumber,
            maxItemsPerPage,
            data -> toProjectGroup(data, null)
        );
    }

    // TODO: javadoc - "Show" + "Languages"
    public CompletableFuture<ProjectGroup> retrieve(final long projectGroupId) {
        final CompletableFuture<JSONObject> projectGroupJsonPromise = apiGetObjectRequest(
            String.format(Locale.ROOT, PROJECT_GROUP_BY_ID_API_URL_TEMPLATE, projectGroupId),
            emptyMap(),
            identity()
        );
        final CompletableFuture<List<JSONObject>> enabledLocalesPromise = apiGetListRequest(
            String.format(Locale.ROOT, PROJECT_GROUP_ENABLED_LOCALES_BY_ID_API_URL_TEMPLATE, projectGroupId),
            emptyMap(),
            identity()
        );
        return projectGroupJsonPromise.thenCombine(enabledLocalesPromise, // TODO: handle the "id not found" case
            (projectGroupJson, enabledLocales) -> toProjectGroup(projectGroupJson, enabledLocales)
        );
    }

    public CompletableFuture<Void> delete(final long projectGroupId) {
        return apiDeleteRequest(
            String.format(Locale.ROOT, PROJECT_GROUP_BY_ID_API_URL_TEMPLATE, projectGroupId),
            emptyMap()
        );
    }

    private static ProjectGroup toProjectGroup(final JSONObject projectGroupJson, final @Nullable List<JSONObject> enabledLocalesJsons) {
        final @Nullable Locale baseLocaleProperty = getOptionalJsonValue(
            projectGroupJson,
            PROJECT_GROUP_BASE_LOCALE_KEY,
            JSONObject.class,
            localeJson -> Locale.forLanguageTag(localeJson.getString(LOCALE_CODE_KEY))
        );
        final @Nullable Locale baseLocale =
            baseLocaleProperty != null
                ? baseLocaleProperty
                : (
                    enabledLocalesJsons != null
                        ? enabledLocalesJsons.stream()
                              .filter(localeJson -> localeJson.getBoolean(PROJECT_GROUP_IS_BASE_LANGUAGE_KEY))
                              .findFirst()
                              .map(
                                  localeJson -> Locale.forLanguageTag(localeJson.getString(LOCALE_CODE_KEY))
                              ).orElse(null)
                        : null
                );
        final @Nullable List<Locale> enabledLocales =
            enabledLocalesJsons != null
                ? enabledLocalesJsons.stream()
                      .map(
                          localeJson -> Locale.forLanguageTag(localeJson.getString(LOCALE_CODE_KEY))
                      ).collect(toUnmodifiableList())
                : null;

        return new ProjectGroup(
            projectGroupJson.getLong(PROJECT_GROUP_ID_KEY),
            projectGroupJson.getString(PROJECT_GROUP_NAME_KEY),
            baseLocale,
            enabledLocales,
            getOptionalJsonValue(projectGroupJson, PROJECT_GROUP_PROJECT_COUNT_KEY, String.class, Long::parseLong)
        );
    }

}
