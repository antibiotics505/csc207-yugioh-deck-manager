package ca.utoronto.csc207.ygo.use_case.deck;

/** The interactor reports exactly one success or failure for each request. */
public interface DeckOutputBoundary {
    void present(DeckResult result);
    void presentError(String message);
}
