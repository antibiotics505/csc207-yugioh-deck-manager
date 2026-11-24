package ca.utoronto.csc207.ygo.interface_adapter;

import ca.utoronto.csc207.ygo.use_case.deck.DeckOutputBoundary;
import ca.utoronto.csc207.ygo.use_case.deck.DeckResult;

public final class DeckPresenter implements DeckOutputBoundary {
    private final DeckViewModel viewModel;

    public DeckPresenter(DeckViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(DeckResult result) {
        result.draft().ifPresent(viewModel::setCurrentDraft);
        result.savedDecks().ifPresent(viewModel::setSavedDecks);
        viewModel.setErrorMessage(null);
    }

    @Override
    public void presentError(String message) {
        viewModel.setErrorMessage(message);
    }
}
