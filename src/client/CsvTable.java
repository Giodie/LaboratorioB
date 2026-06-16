package client;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.BorderLayout;


public class CsvTable {

    public DefaultTableModel tableModel;
    public JTable table;
    public TableRowSorter<DefaultTableModel> sorter;
    public String author;   
    public String title;
    public String year;
    public Utente utente;
    public Libro suggerimento;
    public JPopupMenu popupMenu;
    public ArrayList<Libro> libri = new ArrayList<>();
    public ArrayList<SuggerimentiLibro> suggerimenti = new ArrayList<>();
    public JDialog dialogs = new JDialog();
    public DefaultTableModel suggeritiModel;
    public JTable suggeritiTable;
    public JButton eliminaSuggerimentoBtn;
    private Proxy proxy;
    /**
     * Costruttore della classe CsvTable, utilizzto per mostrare la tabella contente i libri del file CSV.
     */
    public CsvTable() {
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) { // tasto destro
                    int row = table.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        table.setRowSelectionInterval(row, row); // seleziona la riga cliccata
                    }
                }
            }
        });


        popupMenu = new JPopupMenu();
        JMenuItem openItem = new JMenuItem("Informazioni");

        openItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();  // riga selezionata
            if (selectedRow >= 0) {
                visualizzaLibro(selectedRow);
            } else {
                JOptionPane.showMessageDialog(table, "Seleziona un libro prima!");
            }
        });


        popupMenu.add(openItem);
        table.setComponentPopupMenu(popupMenu);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
    }
    /**
     * Metodo per caricare il fileCSV utilizzato per riempire la tabella.
     * @param filePath il path del file CSV
     */
    public void loadCSV(Proxy proxy) {
        this.proxy = proxy;
        String line;
        List<String[]> rows = new ArrayList<>();
        ArrayList<Libro> listaLibri = proxy.getLibri();
        for(int i = 0;i<listaLibri.size();i++){
            String[] cells = {listaLibri.get(i).getTitolo(),listaLibri.get(i).getAutore(),String.valueOf(listaLibri.get(i).getAnnoPubblicazione())};
            rows.add(cells);
        }
        if (!rows.isEmpty()) {
            String[] columnNames = {"Titolo", "Autore", "Anno"};
            tableModel.setColumnIdentifiers(columnNames);
            for (int i = 0; i < rows.size(); i++) {
                tableModel.addRow(rows.get(i));
            }
        }
    }

    /**
     * Metodo per il parsing del file csv.
     * @param line la linea su cui deve essere effettuato il parsing
     * @return
     */
    private String[] parseCSVLine(String line) {
        StringBuilder cell = new StringBuilder();
        List<String> cells = new ArrayList<>();

        boolean inQuotes = false;

        for (char ch : line.toCharArray()) {
            if (ch == '"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                cells.add(cell.toString().trim());
                cell.setLength(0);
            } else {
                cell.append(ch);
            }
        }

        cells.add(cell.toString().trim());
        return cells.toArray(new String[0]);
    }
    /**
     * Metodo per la ricerca di un libro
     * @param titleFilter il titolo del libro
     * @param authorFilter l'autore del libro
     * @param yearFilter    l'anno di pubblicazione del libro.
     */
    public void cercaLibro(String titleFilter, String authorFilter, String yearFilter) {
        ArrayList<Libro> ricerca = proxy.cercaLibri(titleFilter,authorFilter,yearFilter);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        for (Libro l : ricerca) {
            model.addRow(new Object[]{
                    l.getTitolo(),
                    l.getAutore(),
                    l.getAnnoPubblicazione()
            });
        }
    }

    /**
     * Metodo che restituisce la tabella dei libri
     * @return  la tabella dei libri.
     */
    public JTable getTable() {
        return table;
    }
    /**
     * Metodo per visualizzare le informazioni riguardanti un libro.
     * @param row la riga della tabella su cui si trova il libro.
     */
    public void visualizzaLibro(int row) {
        getInfo(table, row); // assicura di prendere titolo, autore, anno dalla riga selezionata
        BookINFO info = new BookINFO(title, author, year,proxy);
    }

    /**
     * Metodo per ottenere i campi dei libri data una riga.
     * @param table la tabella su cui viene effettuata la richiesta
     * @param row la riga della tabella.
     */
    public void getInfo(JTable table, int row) {
        if (row != -1) {
            title = table.getValueAt(row, 0).toString();
            author = table.getValueAt(row, 1).toString();
            year = table.getValueAt(row, 2).toString();

        } else {
            // Handle the case when no row is selected (optional)
            JOptionPane.showMessageDialog(table, "ERROR 10 NO BOOK SELECTED \nPLEASE SELECT A FOOK. \n\tTwat.");

        }
    }
    /** 
     * Aggiunta della voce "AggiungiSuggerimento" alla tabella dei libri nella libreria.
    */ 
    public void aggiungiVoce() {

        if (suggeritiModel == null || suggeritiTable == null) {
            creaPannelloSuggeriti();
        }

        JMenuItem suggerimentoItem = new JMenuItem("Aggiungi Suggerimento");
        suggerimentoItem.addActionListener(e -> aggiungiLibro());
        popupMenu.add(suggerimentoItem);
        table.setComponentPopupMenu(popupMenu);
    }
    

    /**
     * Aggiunge un libro all'ArrayList dei libri, controlla se non si è raggiunto il massimo di 3 libri o se si è già aggiunto quel libro precedentemente.
     */
    private void aggiungiLibro() {
        System.out.println("CIao1");
        loadSuggerimenti();
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return;

        getInfo(table, selectedRow);

        Libro libro = new Libro(title, author, Integer.parseInt(year));
        SwingUtilities.invokeLater(() -> System.out.println("Title: " + title));

        if (suggeritiModel.getRowCount() >= 3) {
            JOptionPane.showMessageDialog(dialogs, "Massimo 3 libri suggeriti per libro");
            return;
        }

        for (int i = 0; i < suggeritiModel.getRowCount(); i++) {
            if (suggeritiModel.getValueAt(i, 0).equals(libro.getTitolo())) {
                JOptionPane.showMessageDialog(dialogs, "Libro già presente");
                return;
            }
        }
        suggeritiModel.addRow(new Object[]{
                libro.getTitolo(),
                libro.getAutore(),
                libro.getAnnoPubblicazione()
        });
        libri.add(libro);
    }

    /**
     * Metodo per aggiungere un suggerimento alla lista dei suggerimenti per poi essere salvata sul file in locale. 
     * Controlla se il suggerimento è nuovo o una modifica di uno precedente.
     */
    public void aggiungiSuggerimento() {
        System.out.println("242");
        SuggerimentiLibro suggerimentiLibro = new SuggerimentiLibro(suggerimento,utente);
        suggerimentiLibro.inserisciSuggerimento(libri);
        if(!proxy.esisteSuggerimentoLibro(utente, suggerimento)){
            
            proxy.aggiungiSuggerimenti(suggerimentiLibro);
        }else{
            proxy.modificaLibroSuggerito(suggerimentiLibro);
        }
        libri.clear();
    }
    /**
     * Elimina un libro suggerito dalla lista.
     */
    public void eliminaSuggerimento() {

        int row = suggeritiTable.getSelectedRow();
        if (row < 0) return;

        String titolo = suggeritiModel.getValueAt(row, 0).toString();
        String autore = suggeritiModel.getValueAt(row, 1).toString();
        int anno = Integer.parseInt(
                suggeritiModel.getValueAt(row, 2).toString()
        );

        libri.removeIf(l ->
                l.getTitolo().equals(titolo) &&
                        l.getAutore().equals(autore) &&
                        l.getAnnoPubblicazione() == anno
        );

        suggeritiModel.removeRow(row);
        eliminaSuggerimentoBtn.setEnabled(false);
    }
    /**
     * Metodo utilizzato per creare il panel dei libri suggeriti.
     * @return Un panel per i libri suggeriti.
     */
    public JPanel creaPannelloSuggeriti() {
        System.out.println("CIao2");
        loadSuggerimenti();

        JPanel panel = new JPanel(new BorderLayout());

        suggeritiModel = new DefaultTableModel(
                new Object[]{"Titolo", "Autore", "Anno"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        suggeritiTable = new JTable(suggeritiModel);
        JScrollPane scroll = new JScrollPane(suggeritiTable);

        for (Libro l : libri) {
            suggeritiModel.addRow(new Object[]{
                    l.getTitolo(),
                    l.getAutore(),
                    l.getAnnoPubblicazione()
            });
        }

        eliminaSuggerimentoBtn = new JButton("Elimina suggerimento");
        eliminaSuggerimentoBtn.setEnabled(false);

        
        suggeritiTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                eliminaSuggerimentoBtn.setEnabled(suggeritiTable.getSelectedRow() >= 0);
            }
        });

        
        eliminaSuggerimentoBtn.addActionListener(e -> eliminaSuggerimento());

        panel.add(new JLabel("Libri suggeriti"), BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(eliminaSuggerimentoBtn, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Carica la lista dei suggerimenti contenuta su un file locale.
     */
    public void loadSuggerimenti() {
        System.out.println("328");
        libri.clear();
        if(proxy.esisteSuggerimentoLibro(utente, suggerimento)){
            libri = proxy.getSuggerimentiLibro(utente, suggerimento).getALLibri();
        }
    }
    /**
     * Metodo utilizzato per sapere il path del file dei libri suggeriti.
     * @return la stringa del path
     */
    private String pathToSuggerimenti(){
        FileFinder fileFinder = new FileFinder();
        String str = String.valueOf(fileFinder.suggerimentiPathDati());
        return str;
    }
    /**
     * Metodo per scrivere la lista di tutti i suggerimenti su un file locale
     * @param suggerimenti la lista da scrivere
     */
    private void  scriviFileSuggerimenti(ArrayList<SuggerimentiLibro> suggerimenti){
        try {
            File file = new File(pathToSuggerimenti());
            FileOutputStream fos=new FileOutputStream(file);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(suggerimenti);
            fos.close();
            oos.close();
        } catch (Exception e) {}

    }
    /**
     * Metodo per settare l'utente che sta effettuando un suggerimento
     * @param utente l'utente che effettua il suggerimento
     */
    public void setUtente(Utente utente) {
        this.utente = utente;
    }
    /**
     * Metodo per settare il libro su cui si esegue un suggerimento
     * @param suggerimento l'oggetto di tipo libro.
     */
    public void setLibroSuggerito(Libro suggerimento) {
        this.suggerimento = suggerimento;
    }
    /**
     * Metodo per pulire la lista in seguito alla conferma o annullamento delle operazioni
     */
    public void clearList(){
        libri.clear();
    }
    /**
     * Metodo per caricare la lista dei suggerimenti precedentemente inseriti.
     */
    public void caricaSuggerimenti(){
        if (suggeritiModel == null || suggeritiTable == null) {
            creaPannelloSuggeriti(); 
        }
        suggeritiModel.setRowCount(0);
        System.out.println("CIao3");
        loadSuggerimenti();
        
        for(int i=0;i<libri.size();i++){
            suggeritiModel.addRow(new Object[]{
                    libri.get(i).getTitolo(),
                    libri.get(i).getAutore(),
                    libri.get(i).getAnnoPubblicazione()

            });
        }

    }
    /**
     * Metodo che restituisce tutti i libri suggeriti da un certo utente dato un libro
     * @param u l'utente
     * @param libro il libro di riferimento
     * @return Un arrayList contenete i libri suggeriti.
     */
    public ArrayList<Libro> getLibriSuggeriti(Utente u, Libro libro){
        System.out.println("CIao4");
        loadSuggerimenti();
        for(SuggerimentiLibro s : suggerimenti){
            if(s.getUtente().equals(u) && s.getLibro().getTitolo().equals(libro.getTitolo())){
                return s.getALLibri();
            }
        }
        return new ArrayList<>();
    }
    public void setProxy(Proxy proxy){
        this.proxy = proxy;
    }


}
