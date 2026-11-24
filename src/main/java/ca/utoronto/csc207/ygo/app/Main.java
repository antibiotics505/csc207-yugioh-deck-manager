package ca.utoronto.csc207.ygo.app;

import ca.utoronto.csc207.ygo.interface_adapter.SelectedCardModel;
import ca.utoronto.csc207.ygo.view.CatalogPanel;
import ca.utoronto.csc207.ygo.view.CollectionPanel;
import ca.utoronto.csc207.ygo.view.DeckPanel;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import ca.utoronto.csc207.ygo.data_access.DeckStore;
import ca.utoronto.csc207.ygo.interface_adapter.DeckController;
import ca.utoronto.csc207.ygo.interface_adapter.DeckPresenter;
import ca.utoronto.csc207.ygo.interface_adapter.DeckViewModel;
import ca.utoronto.csc207.ygo.use_case.deck.DeckInteractor;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Yu-Gi-Oh! Collection and Deck Manager");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setContentPane(createTabs());
            window.setPreferredSize(new Dimension(820, 480));
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }

    static JTabbedPane createTabs() {
        SelectedCardModel selection = new SelectedCardModel();

        DeckViewModel deckViewModel = new DeckViewModel();
        DeckPresenter deckPresenter = new DeckPresenter(deckViewModel);

        // 这里先用内存实现：程序关闭后卡组不会保存。
        // 如果 data_access.DeckStore 需要文件路径，请按它的构造函数修改这一行。
        DeckStore deckStore = new DeckStore();

        DeckInteractor deckInteractor =
                new DeckInteractor(deckStore, deckPresenter);
        DeckController deckController =
                new DeckController(deckInteractor);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        tabs.addTab("Catalog", new CatalogPanel());
        tabs.addTab("Collection", new CollectionPanel(selection));
        tabs.addTab("Decks",
                new DeckPanel(selection, deckViewModel, deckController));

        return tabs;
    }
}
