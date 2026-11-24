package ca.utoronto.csc207.ygo.interface_adapter;

import ca.utoronto.csc207.ygo.entity.Deck;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class DeckViewModel {
    private final PropertyChangeSupport changes =
            new PropertyChangeSupport(this);

    private Deck currentDraft;
    private Map<UUID, Deck> savedDecks = new LinkedHashMap<>();
    private String errorMessage;

    public Optional<Deck> currentDraft() {
        return Optional.ofNullable(currentDraft);
    }

    public Map<UUID, Deck> savedDecks() {
        return new LinkedHashMap<>(savedDecks);
    }

    public Optional<String> errorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public void setCurrentDraft(Deck draft) {
        Deck previous = currentDraft;
        currentDraft = draft;
        changes.firePropertyChange("currentDraft", previous, draft);
    }

    public void setSavedDecks(Map<UUID, Deck> decks) {
        Map<UUID, Deck> previous = savedDecks;
        savedDecks = new LinkedHashMap<>(decks);
        changes.firePropertyChange("savedDecks", previous, savedDecks());
    }

    public void setErrorMessage(String message) {
        String previous = errorMessage;
        errorMessage = message;
        changes.firePropertyChange("errorMessage", previous, message);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }
}
