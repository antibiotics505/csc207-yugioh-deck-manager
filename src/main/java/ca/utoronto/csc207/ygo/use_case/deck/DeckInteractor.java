package ca.utoronto.csc207.ygo.use_case.deck;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.entity.Deck;
import ca.utoronto.csc207.ygo.entity.DeckEntry;
import ca.utoronto.csc207.ygo.use_case.DataAccessException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class DeckInteractor implements DeckInputBoundary {
    private static final int MAX_CARD_QUANTITY = 999;

    private final DeckStore store;
    private final DeckOutputBoundary presenter;

    public DeckInteractor(DeckStore store, DeckOutputBoundary presenter) {
        this.store = store;
        this.presenter = presenter;
    }

    @Override
    public void execute(DeckRequest request) {
        if (request == null) {
            presenter.presentError("Request is missing.");
            return;
        }

        try {
            switch (request.action()) {
                case LIST: {
                    Map<UUID, Deck> savedDecks = store.loadAll();
                    presenter.present(new DeckResult(
                            DeckRequest.Action.LIST, null, savedDecks));
                    break;
                }

                case OPEN: {
                    if (request.deckId() == null) {
                        presenter.presentError("Choose a deck to open.");
                        break;
                    }

                    Map<UUID, Deck> savedDecks = store.loadAll();
                    Deck deck = savedDecks.get(request.deckId());

                    if (deck == null) {
                        presenter.presentError("The selected deck was not found.");
                        break;
                    }

                    presenter.present(new DeckResult(
                            DeckRequest.Action.OPEN, deck, null));
                    break;
                }

                case CREATE: {
                    Deck draft = new Deck(
                            UUID.randomUUID(),
                            "Untitled Deck",
                            new LinkedHashMap<>());

                    presenter.present(new DeckResult(
                            DeckRequest.Action.CREATE, draft, null));
                    break;
                }

                case ADD: {
                    Deck draft = requireDraft(request.draft());
                    Card card = request.card();

                    if (card == null) {
                        presenter.presentError("Choose a card to add.");
                        break;
                    }

                    int quantity = request.quantity();
                    if (quantity <= 0 || quantity > MAX_CARD_QUANTITY) {
                        presenter.presentError("Quantity must be between 1 and 999.");
                        break;
                    }

                    DeckEntry existing = draft.entries().get(card.id());
                    long newQuantity = quantity;

                    if (existing != null) {
                        newQuantity += existing.quantity();
                    }

                    if (newQuantity > MAX_CARD_QUANTITY) {
                        presenter.presentError("A card cannot have more than 999 copies.");
                        break;
                    }

                    Deck updated = draft.withEntry(
                            new DeckEntry(card, (int) newQuantity));

                    presenter.present(new DeckResult(
                            DeckRequest.Action.ADD, updated, null));
                    break;
                }

                case SET: {
                    Deck draft = requireDraft(request.draft());
                    DeckEntry existing = draft.entries().get(request.cardId());

                    if (existing == null) {
                        presenter.presentError("That card is not in the deck.");
                        break;
                    }

                    int quantity = request.quantity();
                    if (quantity <= 0 || quantity > MAX_CARD_QUANTITY) {
                        presenter.presentError("Quantity must be between 1 and 999.");
                        break;
                    }

                    Deck updated = draft.withEntry(existing.withQuantity(quantity));

                    presenter.present(new DeckResult(
                            DeckRequest.Action.SET, updated, null));
                    break;
                }

                case REMOVE: {
                    Deck draft = requireDraft(request.draft());

                    if (!draft.entries().containsKey(request.cardId())) {
                        presenter.presentError("That card is not in the deck.");
                        break;
                    }

                    Deck updated = draft.withoutEntry(request.cardId());

                    presenter.present(new DeckResult(
                            DeckRequest.Action.REMOVE, updated, null));
                    break;
                }

                case SAVE: {
                    Deck draft = requireDraft(request.draft());
                    String name = request.name();

                    if (name == null || name.strip().isEmpty()) {
                        presenter.presentError("Deck name cannot be empty.");
                        break;
                    }

                    Deck namedDeck = draft.withName(name.strip());

                    Map<UUID, Deck> savedDecks =
                            new LinkedHashMap<>(store.loadAll());
                    savedDecks.put(namedDeck.id(), namedDeck);
                    store.saveAll(savedDecks);

                    presenter.present(new DeckResult(
                            DeckRequest.Action.SAVE, namedDeck, savedDecks));
                    break;
                }

                default:
                    presenter.presentError("Unsupported deck action.");
            }
        } catch (DataAccessException e) {
            presenter.presentError(
                    e.getMessage() == null ? "Could not access saved decks." : e.getMessage());
        } catch (IllegalArgumentException e) {
            presenter.presentError(
                    e.getMessage() == null ? "Invalid deck request." : e.getMessage());
        }
    }

    private Deck requireDraft(Deck draft) {
        if (draft == null) {
            throw new IllegalArgumentException("No deck is currently open.");
        }
        return draft;
    }
}