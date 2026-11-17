package ca.utoronto.csc207.ygo.view;

import ca.utoronto.csc207.ygo.entity.Card;
import ca.utoronto.csc207.ygo.entity.Quantity;
import ca.utoronto.csc207.ygo.interface_adapter.SelectedCardModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/** Member B wires owned-copy actions and their display here. */
public final class CollectionPanel extends JPanel {
    public CollectionPanel(SelectedCardModel selection) {
        super(new BorderLayout(8, 8));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        JLabel selected = new JLabel(selectionText(selection));
        selection.addSelectionListener(event -> selected.setText(selectionText(selection)));
        controls.add(selected);
        JSpinner quantity = new JSpinner(new SpinnerNumberModel(1, Quantity.MIN, Quantity.MAX, 1));
        JLabel quantityLabel = new JLabel("Quantity");
        quantityLabel.setLabelFor(quantity);
        controls.add(quantityLabel);
        controls.add(quantity);
        JButton add = new JButton("Add");
        JButton set = new JButton("Set");
        JButton remove = new JButton("Remove");
        for (JButton button : new JButton[] { add, set, remove }) {
            button.setEnabled(false);
            controls.add(button);
        }
        add(controls, BorderLayout.NORTH);
        JTable entries = new JTable(new Object[0][0], new String[] { "Card", "Owned copies" });
        add(new JScrollPane(entries), BorderLayout.CENTER);
        add(new JLabel("Collection editing is awaiting implementation."), BorderLayout.SOUTH);
    }

    private static String selectionText(SelectedCardModel selection) {
        return selection.selectedCard().map(Card::name).orElse("No catalog card selected");
    }
}
