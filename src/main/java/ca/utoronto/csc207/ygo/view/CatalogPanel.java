package ca.utoronto.csc207.ygo.view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;

/** Member A adds the controller, presenter and result selection here. */
public final class CatalogPanel extends JPanel {
    public CatalogPanel() {
        super(new BorderLayout(8, 8));
        JPanel searchBar = new JPanel(new BorderLayout(8, 0));
        JTextField query = new JTextField();
        JLabel queryLabel = new JLabel("Card name");
        queryLabel.setLabelFor(query);
        JButton search = new JButton("Search");
        search.setEnabled(false);
        searchBar.add(queryLabel, BorderLayout.WEST);
        searchBar.add(query, BorderLayout.CENTER);
        searchBar.add(search, BorderLayout.EAST);
        add(searchBar, BorderLayout.NORTH);

        JList<String> results = new JList<>();
        JTextArea details = new JTextArea("Card details and thumbnail preview");
        details.setEditable(false);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(results), new JScrollPane(details));
        split.setResizeWeight(0.45);
        add(split, BorderLayout.CENTER);
        add(new JLabel("Card search is awaiting implementation."), BorderLayout.SOUTH);
    }
}
