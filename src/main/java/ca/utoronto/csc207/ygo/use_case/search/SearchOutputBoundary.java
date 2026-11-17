package ca.utoronto.csc207.ygo.use_case.search;

/** The interactor reports exactly one success or failure for each request. */
public interface SearchOutputBoundary {
    void present(SearchResult result);
    void presentError(String message);
}
