package info.datamuse.onesky;

import info.datamuse.onesky.internal.AbstractOneSkyApi;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import static java.util.Collections.emptyMap;
import static java.util.Objects.requireNonNull;

/**
 * OneSky Project Types API wrapper.
 */
public final class OneSkyProjectTypesApi extends AbstractOneSkyApi {

    /**
     * OneSky Project Type.
     */
    public static final class ProjectType {
        private final String code;
        private final String name;

        /**
         * Project Type constructor.
         *
         * @param code project type code
         * @param name project type name
         */
        public ProjectType(final String code, final String name) {
            this.code = requireNonNull(code);
            this.name = requireNonNull(name);
        }

        /**
         * Returns project type code.
         *
         * @return project type code
         */
        public String getCode() {
            return code;
        }

        /**
         * Returns project type name.
         *
         * @return project type name
         */
        public String getName() {
            return name;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ProjectType)) {
                return false;
            }
            final ProjectType theObj = (ProjectType) obj;
            return
                code.equals(theObj.code)
                && name.equals(theObj.name);
        }

        @Override
        public int hashCode() {
            return code.hashCode();
        }

        @Override
        public String toString() {
            return String.format(
                Locale.ROOT,
                "OneSkyProjectTypesApi.ProjectType{code=%s, name=%s}",
                code, name
            );
        }
    }

    private static final String PROJECT_TYPES_API_URL = API_BASE_URL + "/project-types";

    static final String PROJECT_TYPE_CODE_KEY = "code";
    static final String PROJECT_TYPE_NAME_KEY = "name";

    OneSkyProjectTypesApi(final String apiKey, final String apiSecret, final HttpClient httpClient) {
        super(apiKey, apiSecret, httpClient);
    }

    /**
     * Returns a {@link CompletableFuture promise} for the list of available project types.
     *
     * @return list of project types (promise)
     */
    public CompletableFuture<List<ProjectType>> list() {
        return apiGetListRequest(
            PROJECT_TYPES_API_URL,
            emptyMap(),
            dataItem -> new ProjectType(
                dataItem.getString(PROJECT_TYPE_CODE_KEY),
                dataItem.getString(PROJECT_TYPE_NAME_KEY)
            )
        );
    }

}
