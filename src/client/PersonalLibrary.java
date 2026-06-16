package client;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class PersonalLibrary implements ActionListener {
    private ArrayList<Librerie> libreries = new ArrayList<>();
    private JFrame Library;
    private JTextField nameLibrary;
    private JButton newLibrary;
    private JPanel tablePanel;
    private JTable masterTable;
    private Utente utente;
    private DefaultTableModel masterTableModel;
    private List<LibraryTable> libraryTables = new ArrayList<>();
    private JTable selectedLibraryTable;
    public String bookName;
    public String author;
    public String year;
    private ArrayList<ValutazioniLibro> valutazioni = new ArrayList<>();
    private Proxy proxy;

    /**
     * Costruttore della classe PersonalLibray, inizializza in frame contenente le librerie dell'utente e i vari libri su cui si possono effettuare varie operazioni.
     * @param utente Il possessore delle librerie.
     */
    public PersonalLibrary(Utente utente,Proxy proxy) {
        this.utente = utente;
        this.proxy = proxy;
        Library = new JFrame("Libreria Personale");
        Library.setSize(1000, 600);
        Library.setLocationRelativeTo(null);
        Library.setResizable(false);
        Library.setLayout(new BorderLayout());

        JSplitPane splitPaneVerticale = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPaneVerticale.setResizeWeight(0.2); 

        csvTable = new CsvTable();
        csvTable.loadCSV(proxy);

        masterTable = csvTable.getTable();
        JScrollPane masterScrollPane = new JScrollPane(masterTable);
        masterTableModel = (DefaultTableModel) masterTable.getModel();

        SearchPanel searchPanel = new SearchPanel(csvTable);

        JSplitPane splitPaneAlto = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, masterScrollPane, searchPanel);
        splitPaneAlto.setResizeWeight(0.4); 


        tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        JScrollPane librerieScroll = new JScrollPane(tablePanel);

        JPanel gestioneLibraryPanel = new JPanel();
        gestioneLibraryPanel.setLayout(null);
        gestioneLibraryPanel.setBackground(Color.LIGHT_GRAY);

        JLabel createLibraryLabel = new JLabel("Crea Libreria");
        createLibraryLabel.setFont(new Font("Arial", Font.BOLD, 16));
        createLibraryLabel.setBounds(10, 30, 180, 20); 
        gestioneLibraryPanel.add(createLibraryLabel);

        nameLibrary = new JTextField();
        nameLibrary.setBounds(10, 50, 180, 30);
        gestioneLibraryPanel.add(nameLibrary);

       
        newLibrary = new JButton("+");
        newLibrary.setBounds(200, 50, 50, 30);
        newLibrary.setFocusable(false);
        newLibrary.addActionListener(this);
        gestioneLibraryPanel.add(newLibrary);

        
        JButton removeLibraryButton = new JButton("Rimuovi Libreria");
        removeLibraryButton.setBounds(50, 100, 180, 40);
        removeLibraryButton.setFocusable(false);
        removeLibraryButton.addActionListener(e -> removeSelectedLibrary());
        gestioneLibraryPanel.add(removeLibraryButton);

        JSplitPane splitPaneBasso = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, librerieScroll, gestioneLibraryPanel);
        splitPaneBasso.setResizeWeight(0.3); 
        splitPaneVerticale.setTopComponent(splitPaneAlto);
        splitPaneVerticale.setBottomComponent(splitPaneBasso);

        Library.add(splitPaneVerticale, BorderLayout.CENTER);

        loadLibrariesFromCSV();

        
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem addToLibraryItem = new JMenuItem("Aggiungi alla libreria");
        addToLibraryItem.addActionListener(e -> addToLibraryFromContextMenu());
        popupMenu.add(addToLibraryItem);
        masterTable.setComponentPopupMenu(popupMenu);

        Library.setVisible(true);
    }

    /**
     * Carica le librerie da file.
     */
    private void loadLibrariesFromCSV() {
        resetLibrariesView();
        libreries = proxy.getLibrerieUtente(utente);
        for(int i=0; i<libreries.size(); i++) {
                if(libreries.get(i).getUtente().getUsername().equals(utente.getUsername())) {
                    String libraryName = libreries.get(i).getNome();
                    addNewLibraryTable(libraryName);
                    ArrayList<Libro> temp = libreries.get(i).getAlLibri();
                    //aggiunge i libri alla libreria
                    for (int j = 0; j < temp.size(); j++) {
                        String autore = temp.get(j).getAutore();
                        String titolo = temp.get(j).getTitolo();
                        String anno = Integer.toString(temp.get(j).getAnnoPubblicazione());
                        String[] bookData = {titolo,autore,anno};
                        DefaultTableModel model = (DefaultTableModel) libraryTables.get(libraryTables.size() - 1).table.getModel();
                        model.addRow(bookData);
                    }
                }
            }
    }
    private void resetLibrariesView() {
    tablePanel.removeAll();      
    tablePanel.revalidate();     
    tablePanel.repaint();        
    libraryTables.clear();       
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newLibrary) {
            String libraryName = nameLibrary.getText().trim();
            if (!libraryName.isEmpty()) {
                if(!proxy.esisteLibreriaUtente(utente, libraryName)){
                    proxy.aggiungiLibreria(new Librerie(libraryName,utente));
                    loadLibrariesFromCSV();
                }else{
                   JOptionPane.showMessageDialog(Library, "Hai già una libreria con questo nome"); 
                }
            } else {
                JOptionPane.showMessageDialog(Library, "Dare un nome alla libreria.");
            }
        }
    }
    /**
     * Crea una nuova tabella contenente la nuova tabella.
     * @param libraryName il nome della libreria
     */
    private void addNewLibraryTable(String libraryName) {

        DefaultTableModel tableModel = new DefaultTableModel(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (int i = 0; i < masterTableModel.getColumnCount(); i++) {
            tableModel.addColumn(masterTableModel.getColumnName(i));
        }


        JTable table = new JTable(tableModel);
        libraryTables.add(new LibraryTable(libraryName, table));
        table.setPreferredScrollableViewportSize(new Dimension(500, 100));
        table.setFillsViewportHeight(true);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedLibraryTable = table; 
            }
        });

        JPanel libraryPanel = new JPanel();
        libraryPanel.setLayout(new BorderLayout());
        JLabel libraryLabel = new JLabel("Libreria di: "+ utente.getUsername() +" '" + libraryName + "'");
        libraryPanel.add(libraryLabel, BorderLayout.NORTH);
        libraryPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem removeBookItem = new JMenuItem("Rimuovi Libro");
        removeBookItem.addActionListener(e -> removeSelectedBook(table));
        JMenuItem inserisciValuazione = new JMenuItem("Valuta Libro");
        inserisciValuazione.addActionListener(e -> InserisciValutazione());
        JMenuItem inserisciSuggerimento = new JMenuItem("Inserisci sugerimento");
        inserisciSuggerimento.addActionListener(e -> inserisciSuggerimento());

        popupMenu.add(removeBookItem);
        popupMenu.add(inserisciValuazione);
        popupMenu.add(inserisciSuggerimento);
        table.setComponentPopupMenu(popupMenu);

        tablePanel.add(libraryPanel);
        tablePanel.revalidate();
        tablePanel.repaint();

    }
    /**
     * Aggiunge una libro alla libreria. effettua un controllo per vedere se il libro è gia presente nella libreria su cui si desidera inserire il libro.
     */
    private void addToLibraryFromContextMenu() {
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        for(int i=0;i<libreries.size();i++) {
                JButton button = new JButton(libreries.get(i).getNome());
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        int selectedRow = masterTable.getSelectedRow();
                        Object[] rowData = new Object[masterTableModel.getColumnCount()];
                        for (int i = 0; i < masterTableModel.getColumnCount(); i++) {
                            rowData[i] = masterTableModel.getValueAt(selectedRow, i);
                        }
                        Libro libro = new Libro(rowData[0].toString(),rowData[1].toString(),Integer.valueOf(rowData[2].toString()));
                        for(int j=0;j<libreries.size();j++) {
                            if(libreries.get(j).getNome().equals(button.getText())) {
                                if(!proxy.esisteLibroLibreria(libreries.get(j),libro)) {
                                    libreries.get(j).aggiungiLibro(libro);
                                    proxy.aggiungiLibroLibreria(libreries.get(j),libro);
                                    JOptionPane.showMessageDialog(Library,"Libro aggiunto");
                                    for (LibraryTable lt : libraryTables) {
                                        if (lt.name.equals(button.getText())) {
                                            DefaultTableModel model = (DefaultTableModel) lt.table.getModel();
                                            model.addRow(new Object[]{
                                                    libro.getTitolo(),libro.getAutore(), libro.getAnnoPubblicazione(),});
                                            break;
                                        }
                                    }
                                } else JOptionPane.showMessageDialog(Library,"Libro già presente nella libreria");
                            }
                        }
                    }
                });
                panel.add(button);
            
        }
        frame.add(panel);
        JOptionPane optionPane = new JOptionPane(panel,JOptionPane.PLAIN_MESSAGE,JOptionPane.DEFAULT_OPTION,null,new Object[]{},null);
        JDialog dialog = optionPane.createDialog(frame,"Inserisci");
        dialog.pack();
        dialog.setVisible(true);
    }

    /**
     * Elimina la libreria selezionata.
     */
    private void removeSelectedLibrary() {
        if (selectedLibraryTable == null) {
            JOptionPane.showMessageDialog(Library, "Seleziona una libreria da rimuovere.");
            return;
        }

        int index = -1;
        String libraryName = null;

        for (int i = 0; i < libraryTables.size(); i++) {
            if (libraryTables.get(i).table == selectedLibraryTable) {
                index = i;
                libraryName = libraryTables.get(i).name;
                break;
            }
        }

        if (index == -1 || libraryName == null) {
            JOptionPane.showMessageDialog(Library, "Errore: libreria non trovata.");
            return;
        }

        // Conferma rimozione
        int confirm = JOptionPane.showConfirmDialog(Library,
                "Vuoi davvero eliminare la libreria '" + libraryName + "'?",
                "Conferma",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;
        String finalLibraryName = libraryName;
        for(int i = 0;i<libreries.size();i++){
            if(libreries.get(i).getNome().equals(finalLibraryName)){
                proxy.eliminaLibreria(utente, libreries.get(i));
            }
        }
        libreries.removeIf(l -> l.getUtente().getUsername().equals(utente.getUsername()) && l.getNome().equals(finalLibraryName));

        // 3. Rimuovo il pannello grafico corrispondente
        tablePanel.remove(index);
        libraryTables.remove(index);
        selectedLibraryTable = null;

        tablePanel.revalidate();
        tablePanel.repaint();

        JOptionPane.showMessageDialog(Library, "Libreria rimossa.");
    }

    /**
     * Rimuove un libro dalla libreria
     * @param libraryTable la libreria dalla quale si desidera rimuovere il libro.
     */
    private void removeSelectedBook(JTable libraryTable) {
        int selectedRow = libraryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(Library, "Seleziona un libro da rimuovere.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) libraryTable.getModel();

        String titolo = model.getValueAt(selectedRow, 0).toString();
        String autore = model.getValueAt(selectedRow, 1).toString();
        int anno = Integer.parseInt(model.getValueAt(selectedRow, 2).toString());
        Libro libros = new Libro(titolo,autore,anno);

        String libraryName = null;
        for (LibraryTable lt : libraryTables) {
            if (lt.table == libraryTable) {
                libraryName = lt.name;
                break;
            }
        }

        if (libraryName == null) {
            JOptionPane.showMessageDialog(Library, "Errore: libreria non trovata.");
            return;
        }

        for (Librerie l : libreries) {
            if (l.getNome().equals(libraryName)) {
                proxy.eliminaLibroLibreriaUtente(utente,l,libros);
                l.getAlLibri().removeIf(libro ->
                        libro.getTitolo().equals(titolo) &&
                                libro.getAutore().equals(autore) &&
                                libro.getAnnoPubblicazione() == anno
                );
                break;
            }
        }

        model.removeRow(selectedRow);
        JOptionPane.showMessageDialog(Library, "Libro rimosso.");
    }



    private static class LibraryTable {
        String name;
        JTable table;
        LibraryTable(String name, JTable table) {
            this.name = name;
            this.table = table;
        }
    }
    private String nameFinder(){

        return bookName;
    }
    private String authorFinder(){

        return author;
    }
    private String yearFinder(){
        return year;

    }
    /**
     * Metodo che permette di inserire le valutazioni su un libro. Inizializza il popup su cui è possibile eseguire la valutazione, ed effettua vari controlli per confermare che le valutazioni
     * siano corrette.
     */
    private void InserisciValutazione() {

        if (selectedLibraryTable == null) {
            JOptionPane.showMessageDialog(Library, "Selezionare una libreria.");
            return;
        }

        int selectedRow = selectedLibraryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(Library, "Selezionare un libro.");
            return;
        }

        String titolo = selectedLibraryTable.getValueAt(selectedRow, 0).toString();
        String autore = selectedLibraryTable.getValueAt(selectedRow, 1).toString();
        int anno = Integer.parseInt(selectedLibraryTable.getValueAt(selectedRow, 2).toString());

        Libro libro = new Libro(titolo, autore, anno);
        final JTextField stileField = new JTextField();
        final JTextField notaStileField = new JTextField();
        final JTextField contenutoField = new JTextField();
        final JTextField notaContenutoField = new JTextField();
        final JTextField gradevolezzaField = new JTextField();
        final JTextField notaGradevolezzaField = new JTextField();
        final JTextField originalitaField = new JTextField();
        final JTextField notaOriginalitaField = new JTextField();
        final JTextField edizioneField = new JTextField();
        final JTextField notaEdizioneField = new JTextField();
        final JTextField notaVotoFinaleField = new JTextField();
        if (proxy.esisteValutazioneLibro(utente,libro)){
            ValutazioniLibro val = proxy.getValutazioniLibro(utente,libro);
            stileField.setText(String.valueOf(val.getStile()));
            notaStileField.setText(val.getNoteStile());
            contenutoField.setText(String.valueOf(val.getContenuto()));
            notaContenutoField.setText((val.getNoteContenuto()));
            gradevolezzaField.setText((String.valueOf(val.getGradevolezza())));
            notaGradevolezzaField.setText((val.getNoteGradevolezza()));
            originalitaField.setText((String.valueOf(val.getOriginalita())));
            notaOriginalitaField.setText(val.getNoteOriginalita());
            edizioneField.setText(String.valueOf(val.getEdizione()));
            notaEdizioneField.setText(val.getNoteEdizione());
            notaVotoFinaleField.setText(val.getNoteVotoFinale());
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(12, 2, 5, 5));

        panel.add(new JLabel("Stile (1-5):"));
        panel.add(stileField);
        panel.add(new JLabel("Note stile:"));
        panel.add(notaStileField);

        panel.add(new JLabel("Contenuto (1-5):"));
        panel.add(contenutoField);
        panel.add(new JLabel("Note contenuto:"));
        panel.add(notaContenutoField);

        panel.add(new JLabel("Gradevolezza (1-5):"));
        panel.add(gradevolezzaField);
        panel.add(new JLabel("Note gradevolezza:"));
        panel.add(notaGradevolezzaField);

        panel.add(new JLabel("Originalità (1-5):"));
        panel.add(originalitaField);
        panel.add(new JLabel("Note originalità:"));
        panel.add(notaOriginalitaField);

        panel.add(new JLabel("Edizione (1-5):"));
        panel.add(edizioneField);
        panel.add(new JLabel("Note edizione:"));
        panel.add(notaEdizioneField);

        panel.add(new JLabel("Note voto finale:"));
        panel.add(notaVotoFinaleField);

        JDialog dialog = new JDialog(Library, "Valuta Libro", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();

        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annulla");

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int stile = Integer.parseInt(stileField.getText());
                    int contenuto = Integer.parseInt(contenutoField.getText());
                    int gradevolezza = Integer.parseInt(gradevolezzaField.getText());
                    int originalita = Integer.parseInt(originalitaField.getText());
                    int edizione = Integer.parseInt(edizioneField.getText());

                    if (stile < 1 || stile > 5 ||
                            contenuto < 1 || contenuto > 5 ||
                            gradevolezza < 1 || gradevolezza > 5 ||
                            originalita < 1 || originalita > 5 ||
                            edizione < 1 || edizione > 5 ) {

                        JOptionPane.showMessageDialog(dialog,
                                "I voti devono essere compresi tra 1 e 5");
                        return; 
                    }

                    if (notaStileField.getText().length() > 256 ||
                            notaContenutoField.getText().length() > 256 ||
                            notaGradevolezzaField.getText().length() > 256 ||
                            notaOriginalitaField.getText().length() > 256 ||
                            notaEdizioneField.getText().length() > 256 ||
                            notaVotoFinaleField.getText().length() > 256) {

                        JOptionPane.showMessageDialog(dialog,
                                "Le note non possono superare i 256 caratteri");
                        return; 
                    }
                    double mediaFinale = (stile + contenuto + gradevolezza + originalita + edizione) / 5;
                    ValutazioniLibro vl = new ValutazioniLibro(utente, libro);
                    vl.inserisciValutazioneLibro(
                            stile, notaStileField.getText(),
                            contenuto, notaContenutoField.getText(),
                            gradevolezza, notaGradevolezzaField.getText(),
                            originalita, notaOriginalitaField.getText(),
                            edizione, notaEdizioneField.getText(),
                            mediaFinale, notaVotoFinaleField.getText()
                    );

                    proxy.aggiungiValutazione(vl);
                    

                    JOptionPane.showMessageDialog(dialog, "Valutazione salvata!");
                    dialog.dispose(); 

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog,
                            "Inserire solo numeri nei campi voto");
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.pack();
        dialog.setLocationRelativeTo(Library);
        dialog.setVisible(true);


    }
    /**
     * Permette di inserire dei suggerimenti per un libro. Inizializza il popup sulla quele è possibile effettuare tale azione
     */
    private void inserisciSuggerimento() {
        if (selectedLibraryTable == null) {
            JOptionPane.showMessageDialog(Library, "Selezionare una libreria.");
            return;
        }

        int selectedRow = selectedLibraryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(Library, "Selezionare un libro.");
            return;
        }

        String titoloLibro = selectedLibraryTable.getValueAt(selectedRow, 0).toString();
        String autoreLibro = selectedLibraryTable.getValueAt(selectedRow, 1).toString();
        int annoLibro = Integer.parseInt(selectedLibraryTable.getValueAt(selectedRow, 2).toString());
        Libro libroPrincipale = new Libro(titoloLibro, autoreLibro, annoLibro);

        CsvTable csvTable = new CsvTable();
        csvTable.setUtente(utente);
        csvTable.setLibroSuggerito(libroPrincipale);
        csvTable.setProxy(proxy);
        csvTable.aggiungiVoce();

        csvTable.suggeritiModel = new DefaultTableModel(
                new Object[]{"Titolo", "Autore", "Anno"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        csvTable.suggeritiTable = new JTable(csvTable.suggeritiModel);
        csvTable.eliminaSuggerimentoBtn = new JButton("Elimina suggerimento");
        csvTable.eliminaSuggerimentoBtn.setEnabled(false);
        csvTable.eliminaSuggerimentoBtn.addActionListener(e -> csvTable.eliminaSuggerimento());

        csvTable.loadSuggerimenti();
        csvTable.clearList();
        csvTable.caricaSuggerimenti(); 

        JPopupMenu suggeritiMenu = new JPopupMenu();
        JMenuItem removeSuggestion = new JMenuItem("Elimina suggerimento");

        removeSuggestion.addActionListener(e -> {
            int row = csvTable.suggeritiTable.getSelectedRow();
            if (row >= 0) {
                csvTable.libri.removeIf(l ->
                        l.getTitolo().equals(csvTable.suggeritiModel.getValueAt(row, 0)) &&
                                l.getAutore().equals(csvTable.suggeritiModel.getValueAt(row, 1)) &&
                                l.getAnnoPubblicazione() == Integer.parseInt(csvTable.suggeritiModel.getValueAt(row, 2).toString())
                );
                csvTable.suggeritiModel.removeRow(row);
            }
        });

        suggeritiMenu.add(removeSuggestion);
        csvTable.suggeritiTable.setComponentPopupMenu(suggeritiMenu);

        JScrollPane suggeritiScroll = new JScrollPane(csvTable.suggeritiTable);

        csvTable.loadCSV(proxy);
        JTable foundTable = csvTable.getTable();
        JScrollPane foundScroll = new JScrollPane(foundTable);
        foundScroll.setBorder(BorderFactory.createTitledBorder("Libri trovati"));

        JPopupMenu foundMenu = new JPopupMenu();
        JMenuItem addSuggestion = new JMenuItem("Aggiungi suggerimento");
        JDialog dialog = new JDialog(Library, "Suggerimenti", true);
        addSuggestion.addActionListener(e -> {
            int row = foundTable.getSelectedRow();
            if (row >= 0) {
                if (csvTable.libri.size() >= 3) {
                    JOptionPane.showMessageDialog(dialog, "Puoi suggerire al massimo 3 libri.");
                    return;
                }

                String t = foundTable.getValueAt(row, 0).toString();
                String a = foundTable.getValueAt(row, 1).toString();
                int y = Integer.parseInt(foundTable.getValueAt(row, 2).toString());

                for (Libro l : csvTable.libri) {
                    if (l.getTitolo().equals(t)) {
                        JOptionPane.showMessageDialog(dialog, "Questo libro è già suggerito.");
                        return;
                    }
                }

                Libro nuovoSuggerimento = new Libro(t, a, y);
                csvTable.libri.add(nuovoSuggerimento);
                csvTable.suggeritiModel.addRow(new Object[]{t, a, y});
            }
        });
        foundMenu.add(addSuggestion);
        foundTable.setComponentPopupMenu(foundMenu);

        dialog.setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.4);

        
        JPanel leftPanel = new JPanel(new BorderLayout());
        SearchPanel searchPanel = new SearchPanel(csvTable);
        searchPanel.setPreferredSize(new Dimension(400, 300));
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(suggeritiScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(foundScroll);

        dialog.add(splitPane, BorderLayout.CENTER);

        JPanel buttonsPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Annulla");

        okButton.addActionListener(e -> {
            csvTable.aggiungiSuggerimento();
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> {
            csvTable.clearList(); 
            dialog.dispose();
        });

        buttonsPanel.add(okButton);
        buttonsPanel.add(cancelButton);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);

        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(Library);
        dialog.setVisible(true);
    }
    
    private JSplitPane splitPane;                       
    private CsvTable csvTable;      
    private JTable tabellaLibri;                       
    /**
     * Aggiunge il searchPanel al frame di personalLibrary.
     */
    private void aggiungiSearchPanelPersonalLibrary() {
        JPanel rightPanel = new JPanel(new BorderLayout());

        SearchPanel searchPanel = new SearchPanel(csvTable);
        searchPanel.setPreferredSize(new Dimension(400, 300)); 

        JScrollPane PersonalLibraryScroll = new JScrollPane(tabellaLibri);

        rightPanel.add(searchPanel, BorderLayout.NORTH);
        rightPanel.add(PersonalLibraryScroll, BorderLayout.CENTER);

        splitPane.setRightComponent(rightPanel);

        splitPane.revalidate();
        splitPane.repaint();
    }




}
