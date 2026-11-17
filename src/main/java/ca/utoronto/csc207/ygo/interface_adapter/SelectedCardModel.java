package ca.utoronto.csc207.ygo.interface_adapter;

import ca.utoronto.csc207.ygo.entity.Card;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Optional;

/** The catalog publishes and the other panels read on the Swing event dispatch thread. */
public final class SelectedCardModel {
    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
    private Card selectedCard;

    public Optional<Card> selectedCard() {
        return Optional.ofNullable(selectedCard);
    }

    /** Pass null to clear an obsolete selection. */
    public void setSelectedCard(Card card) {
        Card previous = selectedCard;
        selectedCard = card;
        changes.firePropertyChange("selectedCard", previous, card);
    }

    public void addSelectionListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener("selectedCard", listener);
    }
}
