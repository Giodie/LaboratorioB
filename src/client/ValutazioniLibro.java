
//Giodi Carolo 758379package client;
import java.io.Serializable;

public class ValutazioniLibro implements Serializable{
    private int stile;
    private Utente utente;
    private String noteStile;
    private int originalita;
    private String noteOriginalita;
    private int gradevolezza;
    private String noteGradevolezza;
    private int edizione;
    private String noteEdizione;
    private int contenuto;
    private String noteContenuto;
    private double mediaFinale;
    private String noteVotoFinale;
    private Libro libro;
    
    /**
 * Costruisce un nuovo oggetto ValutazioniLibro associato a un utente e a un libro specificati.
 *
 * @param utente L'utente associato alla valutazione del libro.
 * @param libro Il libro associato alla valutazione.
 */
    public ValutazioniLibro(Utente utente,Libro libro)
    {
        this.libro=libro;
        this.utente=utente;
    }
/**
 * Inserisce all'interno di un oggetto valutazione libro le varie valutazioni possibili
 *
 * @param stile Valutazione dello stile del libro.
 * @param noteStile Note sulla valutazione dello stile.
 * @param contenuto Valutazione del contenuto del libro.
 * @param noteContenuto Note sulla valutazione del contenuto.
 * @param gradevolezza Valutazione della gradevolezza del libro.
 * @param noteGradevolezza Note sulla valutazione della gradevolezza.
 * @param originalita Valutazione dell'originalità del libro.
 * @param noteOriginalita Note sulla valutazione dell'originalità.
 * @param edizione Valutazione dell'edizione del libro.
 * @param noteEdizione Note sulla valutazione dell'edizione.
 * @param noteVotoFinale Note sul voto finale.
 */
    public void inserisciValutazioneLibro(int stile,String noteStile,int contenuto,String noteContenuto,int gradevolezza,String noteGradevolezza,int originalita,String noteOriginalita,int edizione,String noteEdizione,double mediaFinale,String noteVotoFinale){
        this.stile=stile;
        this.contenuto=contenuto;
        this.gradevolezza=gradevolezza;
        this.originalita=originalita;
        this.edizione=edizione;
        this.mediaFinale=mediaFinale;
        this.noteStile=noteStile;
        this.noteContenuto=noteContenuto;
        this.noteGradevolezza=noteGradevolezza;
        this.noteOriginalita=noteOriginalita;
        this.noteEdizione=noteEdizione;
        this.noteVotoFinale=noteVotoFinale;
    }

/**
 * Restituisce lo stile del libro.
 *
 * @return La valutazione dello stile del libro.
 */
    public int getStile(){
        return this.stile;
    }
/**
 * Restituisce il contenuto del libro.
 *
 * @return La valutazione del contenuto del libro.
 */
    public int getContenuto(){
        return this.contenuto;
    }
/**
 * Restituisce la gradevolezza del libro.
 *
 * @return La valutazione della gradevolezza del libro.
 */
    public int getGradevolezza(){
        return this.gradevolezza;
    }
/**
 * Restituisce l'originalità del libro.
 *
 * @return La valutazione dell'originalità del libro.
 */
    public int getOriginalita(){
        return this.originalita;
    }
/**
 * Restituisce il voto dell'edizione del libro.
 *
 * @return Il numero di edizione del libro.
 */
    public int getEdizione(){
        return this.edizione;
    }
/**
 * Restituisce il voto finale del libro che viene calcolato come media degli altri voti.
 *
 * @return Il voto finale assegnato al libro.
 */
    public double getMediaFinale(){
        return this.mediaFinale;
    }
/**
 * Restituisce le note sulla valutazione dello stile del libro.
 *
 * @return Le note sulla valutazione dello stile del libro.
 */
    public String getNoteStile(){
        return this.noteStile;
    }
/**
 * Restituisce le note sulla valutazione del contenuto del libro.
 *
 * @return Le note sulla valutazione del contenuto del libro.
 */
    public String getNoteContenuto(){
        return this.noteContenuto;
    }
/**
 * Restituisce le note sulla valutazione della gradevolezza del libro.
 *
 * @return Le note sulla valutazione della gradevolezza del libro.
 */
    public String getNoteGradevolezza(){
        return this.noteGradevolezza;
    }
/**
 * Restituisce le note sulla valutazione dell'originalità del libro.
 *
 * @return Le note sulla valutazione dell'originalità del libro.
 */
    public String getNoteOriginalita(){
        return this.noteOriginalita;
    }
/**
 * Restituisce le note sulla valutazione dell'edizione del libro.
 *
 * @return Le note sulla valutazione dell'edizione del libro.
 */
    public String getNoteEdizione(){
        return this.noteEdizione;
    }
/**
 * Restituisce le note sulla valutazione finale del libro.
 *
 * @return Le note sulla valutazione finale del libro.
 */
    public String getNoteVotoFinale(){
        return this.noteVotoFinale;
    }
/**
 * Restituisce il libro associato alla valutazione.
 *
 * @return Il libro associato a questa valutazione.
 */
    public Libro getLibro(){
        return this.libro;
    }
/**
 * Restituisce l'utente che ha valutato il libro.
 *
 * @return L'utente associato a questa valutazione.
 */
    public Utente getUtente(){
        return this.utente;
    }
}