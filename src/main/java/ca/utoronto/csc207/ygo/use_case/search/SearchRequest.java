package ca.utoronto.csc207.ygo.use_case.search;

/** Raw input. The search interactor owns trimming and validation. */
public final class SearchRequest {
    private final String query;

    public SearchRequest(String query) {
        this.query = query;
    }

    public String query() { return query; }
}
