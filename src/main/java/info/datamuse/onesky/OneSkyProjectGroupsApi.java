package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import javax.annotation.Nullable;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

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

        /**
         * Project Group constructor.
         *
         * @param id project group id
         * @param name project group name
         * @param baseLocale optional base locale
         */
        public ProjectGroup(final long id, final String name, final @Nullable Locale baseLocale) {
            this.id = id;
            this.name = requireNonNull(name);
            this.baseLocale = baseLocale;
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
                && Objects.equals(baseLocale, theObj.baseLocale);
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
            data -> new ProjectGroup(
                data.getLong(PROJECT_GROUP_ID_KEY),
                data.getString(PROJECT_GROUP_NAME_KEY),
                data.has(PROJECT_GROUP_BASE_LOCALE_KEY) ? Locale.forLanguageTag(data.getJSONObject(PROJECT_GROUP_BASE_LOCALE_KEY).getString(OneSkyLocalesApi.LOCALE_CODE_KEY)) : null
            )
        );
    }

    // TODO: implement List, Retrieve ("Show"), Delete, Languages

}
