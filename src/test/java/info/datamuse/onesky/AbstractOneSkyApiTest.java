package info.datamuse.onesky;

import static java.net.http.HttpClient.newHttpClient;

public abstract class AbstractOneSkyApiTest {

    private static final String TEST_API_KEY = "OythOHK5h1xF1lXelhehPvurwYOPFV1j";
    private static final String TEST_API_SECRET = "VW4ixQsYVt86ugrKPu6eb6xVBcTiy7aj";

    protected final OneSkyClient getOneSkyClient() {
        return new OneSkyClient(TEST_API_KEY, TEST_API_SECRET, newHttpClient());
    }

}
