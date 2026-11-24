package ca.utoronto.csc207.ygo.interface_adapter;

import ca.utoronto.csc207.ygo.use_case.deck.DeckInputBoundary;
import ca.utoronto.csc207.ygo.use_case.deck.DeckRequest;

import java.util.UUID;

public final class DeckController {
    private final DeckInputBoundary interactor;

    public DeckController(DeckInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void listDecks() {
        interactor.execute(DeckRequest.list());
    }

    public void openDeck(UUID deckId) {
        interactor.execute(DeckRequest.open(deckId));
    }

    public void createDeck() {
        interactor.execute(DeckRequest.create());
    }

    public void addCard(
            ca.utoronto.csc207.ygo.entity.Deck draft,
            ca.utoronto.csc207.ygo.entity.Card card,
            int quantity) {
        interactor.execute(DeckRequest.add(draft, card, quantity));
    }

    public void setCardQuantity(
            ca.utoronto.csc207.ygo.entity.Deck draft,
            long cardId,
            int quantity) {
        interactor.execute(DeckRequest.set(draft, cardId, quantity));
    }

    public void removeCard(
            ca.utoronto.csc207.ygo.entity.Deck draft,
            long cardId) {
        interactor.execute(DeckRequest.remove(draft, cardId));
    }

    public void saveDeck(
            ca.utoronto.csc207.ygo.entity.Deck draft,
            String name) {
        interactor.execute(DeckRequest.save(draft, name));
    }
}
