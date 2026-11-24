package ca.utoronto.csc207.ygo.use_case.search;

import ca.utoronto.csc207.ygo.use_case.DataAccessException;

public final class SearchInteractor implements SearchInputBoundary {
    private static final int RESULT_LIMIT = 25;

    private final CardCatalog catalog;
    private final SearchOutputBoundary output;

    public SearchInteractor(CardCatalog catalog, SearchOutputBoundary output) {
        this.catalog = catalog;
        this.output = output;
    }

    @Override
    public void execute(SearchRequest request) {
        String query = request.query().strip();
        if (query.isEmpty()) {
            output.presentError("Enter a card name.");
            return;
        }

        SearchResult result;
        try {
            result = catalog.search(query, RESULT_LIMIT);
        } catch (DataAccessException exception) {
            output.presentError("Could not search the card catalog.");
            return;
        }
        output.present(result);
    }
}
