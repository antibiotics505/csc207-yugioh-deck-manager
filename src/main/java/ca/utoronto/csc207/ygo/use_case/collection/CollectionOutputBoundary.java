package ca.utoronto.csc207.ygo.use_case.collection;

/** The interactor reports exactly one success or failure for each request. */
public interface CollectionOutputBoundary {
    void present(CollectionResult result);
    void presentError(String message);
}
