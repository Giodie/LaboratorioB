package client;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class BookINFO {

    private JFrame frame;
    private ArrayList<ValutazioniLibro> valutazioni;

    // Componenti GUI
    private DefaultListModel<String> utentiModel;
    private JList<String> utentiList;
    private JScrollPane utentiScroll;
    private DefaultTableModel medieModel;
    private JTable medieTable;
    private JScrollPane medieScroll;

    private Libro libro;
    private ArrayList<Utente> utenti;
    private Proxy proxy;

    /**
    *Costruttore di BookInfo che prende come input il nome del libro, l'autore e l'anno per far apparire tutte le informazioni
    *riguardanti il libro oltre a chi ha inserito delle valutazioni o consigliato altri libri rispetto al libro.
    *
    *@param bookName il titolo del libro
    *@param author l'autore del libro
    *@param year   l'anno di pubblicazione del libro
    */
    public BookINFO(String bookName, String author, String year,Proxy proxy) {
        this.proxy = proxy;
        Color lilla = new Color(186, 156, 216); 
        Color lillaSoft = new Color(230, 220, 245);

        frame = new JFrame("Informazioni Libro");
        frame.setSize(980, 450);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.setResizable(true);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, lilla),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        JLabel headerLabel = new JLabel(
                "<html><div style='width:750px;'>"
                        + "<b>" + bookName + "</b><br>"
                        + "<span style='font-size:12px;color:#555;'>"
                        + author + " · " + year
                        + "</span></div></html>"
        );
        headerLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        topPanel.add(headerLabel, BorderLayout.WEST);
        frame.add(topPanel, BorderLayout.NORTH);

      
        utentiModel = new DefaultListModel<>();
        utentiList = new JList<>(utentiModel);
        utentiList.setFont(new Font("SansSerif", Font.PLAIN, 13));
        utentiList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        utentiList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                label.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

                if (isSelected) {
                    label.setBackground(lillaSoft);
                    label.setForeground(Color.BLACK);
                }

                return label;
            }
        });

        utentiScroll = new JScrollPane(utentiList);
        utentiScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(lilla),
                "Utenti",
                0, 0,
                new Font("SansSerif", Font.BOLD, 13),
                lilla
        ));

        medieModel = new DefaultTableModel(new Object[]{"Campo", "Media"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medieTable = new JTable(medieModel);
        medieTable.setRowHeight(24);
        medieTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        medieTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        medieTable.getTableHeader().setBackground(lilla);
        medieTable.getTableHeader().setForeground(Color.WHITE);

        // zebra rows
        medieTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : lillaSoft);
                }
                return c;
            }
        });

        medieScroll = new JScrollPane(medieTable);
        medieScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(lilla),
                "Valutazioni medie",
                0, 0,
                new Font("SansSerif", Font.BOLD, 13),
                lilla
        ));

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        centerPanel.add(utentiScroll);
        centerPanel.add(medieScroll);
        frame.add(centerPanel, BorderLayout.CENTER);

        libro = new Libro(bookName, author, Integer.parseInt(year));

        loadValutazioni();

        utenti = getUtentiPerLibro(libro);
        utentiModel.clear();

        if (utenti.isEmpty()) {
            utentiModel.addElement(
                    "Ancora nessun utente ha fornito maggiori informazioni su questo libro"
            );
            utentiList.setEnabled(false);
        } else {
            utentiList.setEnabled(true);
            for (Utente u : utenti) {
                utentiModel.addElement(descrizioneUtente(u));
            }

            utentiList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int index = utentiList.getSelectedIndex();
                    if (index >= 0 && index < utenti.size()) {
                        mostraDettagliUtente(
                                utenti.get(index).getUsername(),
                                libro
                        );
                    }
                }
            });
        }

        calcolaMediePerLibro(libro, medieModel);
        aggiungiStellineVotoFinale();

        frame.setVisible(true);
    }



    /**
     * Carica la lista delle varie valutazioni da un file di test contenente tale lista.
     * 
     */
    private void loadValutazioni() {
        File file = new File(pathToValutazioni());
        valutazioni = new ArrayList<>();

        if (!file.exists() || file.length() == 0) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            valutazioni = (ArrayList<ValutazioniLibro>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Metodo utilizzato per conoscere il path del file sulle valutazioni
     * 
     * @return La stringa contenente il path del file sulle valutazioni. 
     * */
    private String pathToValutazioni() {
        FileFinder fileFinder = new FileFinder();
        return String.valueOf(fileFinder.valutazioniPathDati());
    }

    /**
     * Metodo che restituisce una lista di tutte le valutazioni di un certo libro
     * 
     * @param libro il libro di cui si vuole sapere tutte le valutazioni
     * @return l'ArrayList contiene tutte le valutazioni di un certo libro.
     */
    private ArrayList<ValutazioniLibro> getValutazioniPerLibro(Libro libro) {
        ArrayList<ValutazioniLibro> list = new ArrayList<>();
        for (ValutazioniLibro v : valutazioni) {
            if (v.getLibro().equalsLibro(libro)) {
                list.add(v);
            }
        }
        return list;
    }

    /**
     * Metodo che restituisce tutti gli utenti che hanno valutato un certo libro
     * 
     *@param libro il libro di cui si vuole sapere chi ha effettuato una valutazione
     * @return l'ArrayList di tutti gli utente che hanno effettuato una valutazione su un certo libro.
     */
    private ArrayList<Utente> getUtentiPerLibro(Libro libro) {
        ArrayList<Utente> list = new ArrayList<>();

        
        ArrayList<Utente> listaSuggeriti = proxy.getUtentiSuggerimenti(libro);
        for(int i=0;i<listaSuggeriti.size();i++){
            list.add(listaSuggeriti.get(i));
        }
        ArrayList<Utente> listaValutazioni = proxy.getUtentiValutazioni(libro);
        for(int i=0;i<listaValutazioni.size();i++){
            boolean b = true;
            for(int j=0;j<list.size();j++){
                if(list.get(j).getCF().equals(listaValutazioni.get(i).getCF())){
                    b = false;
                }
            }
            if(b){
                list.add(listaValutazioni.get(i));
            }
        }
        return list;
    }

    /**
     * Metodo per il calcolo delle varie medie di tutti gli utenti di un certo libro
     * 
     * @param libro     il libro su cui si desidera calcolare la media.
     * @param model     il TableModel su cui si vuole fare apparire le varie medie.
     */
    private void calcolaMediePerLibro(Libro libro, DefaultTableModel model) {
        ArrayList<Integer> valori = proxy.getMediaValutazione(libro);

        model.setRowCount(0);
        model.addRow(new Object[]{"Stile", valori.get(0)});
        model.addRow(new Object[]{"Contenuto", valori.get(1)});
        model.addRow(new Object[]{"Gradevolezza", valori.get(2)});
        model.addRow(new Object[]{"Originalità", valori.get(3)});
        model.addRow(new Object[]{"Edizione", valori.get(4)});
        model.addRow(new Object[]{"Voto Finale", valori.get(5)});
    }

    /**
     * Metodo per far apparire le varie informazioni su un libro di un certo utente ovvero i possibili suggerimenti o
     * le possibili valutazioni
     * 
     * @param usernameUtente l'username dell'utente
     * @libro il libro su cui si vuole sapere le informazioni.
     */
    private void mostraDettagliUtente(String usernameUtente, Libro libro) {
        Utente u = null;
        for (Utente user : utenti) {
            if (user.getUsername().equals(usernameUtente)) {
                u = user;
                break;
            }
        }

        if (u == null) {
            JOptionPane.showMessageDialog(frame, "Utente non trovato");
            return;
        }

        JDialog dialog = new JDialog(frame, "Dettagli Utente", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(frame);

        JTextArea info = new JTextArea();
        info.setEditable(false);
        StringBuilder sb = new StringBuilder();

        sb.append("Valutazioni:\n");
        ValutazioniLibro v = proxy.getValutazioniLibro(u,libro);
        if (v != null) {
            sb.append("Stile: ").append(v.getStile()).append("\n");
            sb.append("Contenuto: ").append(v.getContenuto()).append("\n");
            sb.append("Gradevolezza: ").append(v.getGradevolezza()).append("\n");
            sb.append("Originalità: ").append(v.getOriginalita()).append("\n");
            sb.append("Edizione: ").append(v.getEdizione()).append("\n");
            sb.append("Voto Finale: ").append(v.getMediaFinale()).append("\n\n");
        }

        
        sb.append("Libri suggeriti:\n");
        SuggerimentiLibro suggerimenti = proxy.getSuggerimentiLibro(u,libro);
        if (suggerimenti != null ){
            for (int i = 0 ; i<suggerimenti.getALLibri().size(); i++) {
                sb.append("-").append(suggerimenti.getALLibri().get(i).getTitolo()).append("\n");
            }   
        }
        

        info.setText(sb.toString());
        dialog.add(new JScrollPane(info));
        dialog.setVisible(true);
    }
    private String descrizioneUtente(Utente u) {
        boolean haValutato = false;
        boolean haSuggerito = false;

        for (ValutazioniLibro v : valutazioni) {
            if (v.getUtente().getUsername().equals(u.getUsername()) &&
                    v.getLibro().getTitolo().equals(libro.getTitolo())) {
                haValutato = true;
                break;
            }
        }

        CsvTable csv = new CsvTable();
        //inserire chi ha suggerito il libro
        if (haValutato && haSuggerito)
            return u.getUsername() + " ha valutato questo libro e ne ha suggeriti altri";
        if (haValutato)
            return u.getUsername() + " ha valutato questo libro";
        if (haSuggerito)
            return u.getUsername() + " ha suggerito altri libri";

        return u.getUsername();
    }
    /**
     * Metodo per caricare la lista contenuta sul file dei suggerimenti
     * 
     * @return l'ArrayList di tutti i suggerimenti di tutti i libri
     */
    
    /**
     * Metodo per sapere qual è il path per il file sui suggerimenti.
     * 
     * @return una stringa contente il path.
     */
    private String pathToSuggerimenti(){
        FileFinder fileFinder = new FileFinder();
        String str = String.valueOf(fileFinder.suggerimentiPathDati());
        return str;
    }

    /**
     * metodo per aggiungere delle stelle vicino alla media del voto finale.
     * 
     */
    private void aggiungiStellineVotoFinale() {
        for (int i = 0; i < medieModel.getRowCount(); i++) {
            if (medieModel.getValueAt(i, 0).equals("Voto Finale")) {
                double voto = Double.parseDouble(
                        medieModel.getValueAt(i, 1).toString()
                );

                int stelle = (int) Math.round(voto);
                StringBuilder sb = new StringBuilder();
                for (int s = 0; s < stelle; s++) sb.append("★");

                medieModel.setValueAt(
                        String.format("%.1f  %s", voto, sb),
                        i, 1
                );
                break;
            }
        }
    }



}
