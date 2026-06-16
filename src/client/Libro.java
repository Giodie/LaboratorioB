package client;
import java.io.Serializable;
public class Libro implements Serializable{
    private String titolo;
    private String autore;
    private int annoPubblicazione;

    /**
     * Costruttore della classe Libro che inizializza un'oggetto di quest'ultima tramite i campi titolo, autore e anno
     * 
     * @param titolo    il titolo del libro
     * @param autore    l'autore del libro
     * @param annoPubblicazione     l'anno di pubblicazione del libro
     */
    public Libro(String titolo,String autore,int annoPubblicazione){
        this.titolo=titolo;
        this.autore=autore;
        this.annoPubblicazione = annoPubblicazione;
    }
/**
 * Restituisce il nome dell'autore del libro.
 *
 * @return Una stringa contenente il nome dell'autore del libro.
 */
    public String getAutore(){
        return this.autore;
    }
    /**
 * Restituisce il titolo del libro.
 *
 * @return Una stringa contenente il titolo del libro.
 */
    public String getTitolo(){
        return this.titolo;
    }
    /**

    /**
 * Restituisce l'anno di pubblicazione del libro.
 *
 * @return L'anno di pubblicazione del libro.
 */
    public int getAnnoPubblicazione(){
        return this.annoPubblicazione;
    }
    /**
     * Confronta il libro che chiama il metodo con un libro passato come input.
     * 
     * @return true se i libri sono uguali false altrimenti.
     */
    public boolean equalsLibro(Libro l){
        if(this.titolo.equals(l.getTitolo()) && this.autore.equals(l.getAutore()) && this.annoPubblicazione == l.getAnnoPubblicazione()){
            return true;

        }else return false;
    }

}
