package ca.utoronto.csc207.ygo.view;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.entity.Quantity;
import ca.utoronto.csc207.ygo.interface_adapter.SelectedCardModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

/** Member C wires draft editing and saved-deck selection here. */
public final class DeckPanel extends JPanel {
    public DeckPanel(SelectedCardModel selection) {
        super(new BorderLayout(8, 8));
        JPanel controls = new JPanel(new GridLayout(0, 1));
        JPanel deckControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JComboBox<String> savedDecks = new JComboBox<>();
        savedDecks.setEnabled(false);
        JLabel savedLabel = new JLabel("Saved decks");
        savedLabel.setLabelFor(savedDecks);
        deckControls.add(savedLabel);
        deckControls.add(savedDecks);
        JButton newDeck = new JButton("New");
        newDeck.setEnabled(false);
        deckControls.add(newDeck);
        JTextField deckName = new JTextField(18);
        JLabel nameLabel = new JLabel("Deck name");
        nameLabel.setLabelFor(deckName);
        deckControls.add(nameLabel);
        deckControls.add(deckName);
        JButton save = new JButton("Save");
        save.setEnabled(false);
        deckControls.add(save);
        controls.add(deckControls);

        JPanel entryControls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel selected = new JLabel(selectionText(selection));
        selection.addSelectionListener(event -> selected.setText(selectionText(selection)));
        entryControls.add(selected);
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, Quantity.MIN, Quantity.MAX, 1));
        JLabel quantityLabel = new JLabel("Quantity");
        quantityLabel.setLabelFor(quantity);
        entryControls.add(quantityLabel);
        entryControls.add(quantity);
        for (String action : new String[] { "Add", "Set", "Remove" }) {
            JButton button = new JButton(action);
            button.setEnabled(false);
            entryControls.add(button);
        }
        controls.add(entryControls);
        add(controls, BorderLayout.NORTH);

        JTable entries = new JTable(new Object[0][0], new String[] { "Card", "Planned copies" });
        add(new JScrollPane(entries), BorderLayout.CENTER);
        add(new JLabel("Total copies: 0  |  Deck editing is awaiting implementation."), BorderLayout.SOUTH);
    }

    private static String selectionText(SelectedCardModel selection) {
        return selection.selectedCard().map(Card::name).orElse("No catalog card selected");
    }
}
