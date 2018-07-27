package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;
import info.datamuse.onesky.internal.JsonUtils;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.net.http.HttpClient;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static info.datamuse.onesky.internal.JsonUtils.getOptionalJsonValue;
import static info.datamuse.onesky.internal.ListUtils.optionalListRequireNonNullItems;
import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

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
        private final @Nullable Integer projectCount;

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
            final @Nullable Integer projectCount
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

        public @Nullable Integer getProjectCount() {
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

    private static final String PROJECT_GROUP_NAME_PARAM = "name";
    private static final String PROJECT_GROUP_BASE_LOCALE_PARAM = "locale";

    static final String PROJECT_GROUP_ID_KEY = "id";
    static final String PROJECT_GROUP_NAME_KEY = PROJECT_GROUP_NAME_PARAM;
    static final String PROJECT_GROUP_BASE_LOCALE_KEY = "base_language";
    static final String PROJECT_GROUP_PROJECT_COUNT_KEY = "project_count";

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

//    public CompletableFuture<ProjectGroup> retrieve() {
//        return apiGetPaginatedListOfObjectsRequest(
//            PROJECT_GROUPS_API_URL,
//            emptyMap(),
//            OneSkyProjectGroupsApi::jsonToProjectGroup
//        );
//    }

    // TODO: implement Retrieve ("Show" + "Languages"), Delete

    private static ProjectGroup toProjectGroup(final JSONObject projectGroupJson, final @Nullable List<Locale> enabledLocales) {
        return new ProjectGroup(
            projectGroupJson.getLong(PROJECT_GROUP_ID_KEY),
            projectGroupJson.getString(PROJECT_GROUP_NAME_KEY),
            getOptionalJsonValue(projectGroupJson, PROJECT_GROUP_BASE_LOCALE_KEY, JSONObject.class, localeJson -> Locale.forLanguageTag(localeJson.getString(OneSkyLocalesApi.LOCALE_CODE_KEY))),
            enabledLocales,
            getOptionalJsonValue(projectGroupJson, PROJECT_GROUP_PROJECT_COUNT_KEY, Integer.class)
        );
    }

}
