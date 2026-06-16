package client;


import java.io.File;

public class FileFinder {
    File file = new File(".");
    String path = file.getAbsolutePath();
    /**
     * Costruttore della classe FileFinder
     */
    public FileFinder(){
    }

    /**
     * Restituisce il path che porta al file contenete i libri.
     * @return path del file libri
     */
    public File LibrifilePath(){
        String sep = File.separator;
        File LibriDati = new File(path+sep+"data"+sep+"Libri.dati.csv");
        return LibriDati;
    }
    /**
     * Restituisce il path che porta al file contenete gli utenti registrati
     * @return path del file sugli utenti
     */
    public File UtentiRegistrati(){
        String sep = File.separator;
        File Utenti = new File(path+sep+"data"+sep+ "UtentiRegistrati.dati.csv");
        return Utenti;
    }
    /**
     * Restituisce il path che porta al file contenete le librerie.
     * @return path del file librerie
     */
    public File LibreriePathDati(){
        String sep = File.separator;
        File Librerie = new File(path+sep+"data"+sep+"Librerie.dati.txt");
        return Librerie;
    }
    /**
     * Restituisce il path che porta al file contenete il dataset Originale.
     * @return path del file del dataset.
     */
    public File MasterCSVPath(){
        String sep = File.separator;
        File MasterCSV = new File(path+sep+"data"+sep+ "Dataset.csv");
        return MasterCSV;
    }
    /**
     * Restituisce il path che porta al file contenete le valutazioni dei libri.
     * @return path del file valutazioniLibri
     */
    public File valutazioniPathDati(){
        String sep = File.separator;
        File fileValutazioni= new File(path+sep+"data"+sep+"ValutazioniLibri.dati.txt");
        return fileValutazioni;
    }
    /**
     * Restituisce il path che porta al file contenete i suggerimenti dei libri.
     * @return path del file suggerimentiLibri
     */
    public File suggerimentiPathDati(){
        String sep = File.separator;
        File fileSuggerimenti= new File(path+sep+"data"+sep+"SuggerimentiLibri.dati.txt");
        return fileSuggerimenti;
    }
}
