package client;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
public class Frame {

    JFrame frame;

    /**
     * Costruttore della classe Frame. Inizializza il frame principale dell'applicazione.
     */
    Proxy proxy;
    public Frame() {
        proxy = new Proxy();
        new Thread(() -> {
            proxy.connect();
            System.out.println("Connesso al server!");
        }).start();
        frame = new JFrame("Book Recommender");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1440, 720);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setLayout(new BorderLayout());

        ToolBar toolBar = new ToolBar(proxy);

        CsvTable csvTable = new CsvTable();
        proxy.waitUntilConnected();
        csvTable.loadCSV(proxy);

        JTable table = csvTable.getTable();

        JScrollPane scrollPane = new JScrollPane(table);

        SearchPanel searchPanel = new SearchPanel(csvTable); // invia la classe csvTable a searchPanel
        searchPanel.setPreferredSize(new Dimension(300, 300));

        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(searchPanel, BorderLayout.EAST);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }
    /**
     * Metodo che restituisce il path del file csv sui libri.
     * @return il path del file csv.
     */
    String getCSVfilePath(){
        FileFinder fileFinder = new FileFinder();
        String str = String.valueOf(fileFinder.LibrifilePath());

        return str;
    }
}
