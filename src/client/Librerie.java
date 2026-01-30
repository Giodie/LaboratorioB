//Giodi Carolo 758379
package client;
import java.io.Serializable;
import java.util.ArrayList;

public class Librerie implements Serializable{
    private Utente utente;
    private ArrayList<Libro> alLibri;
    private String nome;

/**
 * Costruisce un oggetto Librerie dato il nome della libreria, l'utente che possiede la libreria.
 *
 * @param nome Il nome della libreria.
 * @param utente L'utente associato alla libreria.
 */
    public Librerie(String nome,Utente utente){
        alLibri = new ArrayList<>();
        this.utente=utente;
        this.nome=nome;    
    }
    
/**
 * Aggiunge un libro alla lista dei libri della libreria.
 *
 * @param l Il libro da aggiungere alla libreria.
 */
    public void aggiungiLibro(Libro l){
        this.alLibri.add(l);
    }
/**
 * Restituisce l'utente associato alla libreria.
 *
 * @return L'oggetto Utente associato alla libreria.
 */
    public Utente getUtente(){
        return this.utente;
    }
/**
 * Restituisce il nome della libreria.
 *
 * @return Il nome della libreria.
 */
    public String getNome(){
        return this.nome;
    }
/**
 * Restituisce l'elenco dei libri presenti nella libreria.
 *
 * @return Un ArrayList contenente i libri presenti nella libreria.
 */
    public ArrayList<Libro> getAlLibri(){
        return this.alLibri;
    }
}