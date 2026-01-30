//Giodi Carolo 758379
package client;
import java.io.Serializable;
public class Utente implements Serializable {

    private String nome;
    private String cognome;
    private String email;
    private String username;
    private String password;
    private String cf;

    /**
     * Costruttore per la classe Utente che inizializza un nuovo utente con nome, cognome, email, username e password.
     *
     * @param nome     Il nome dell'utente.
     * @param cognome  Il cognome dell'utente.
     * @param codiceFiscale il codice Fiscale dell'utente
     * @param email    L'email dell'utente.
     * @param username Lo username dell'utente.
     * @param password La password dell'utente.
     */
    public Utente(String nome, String cognome, String codiceFiscale, String email, String username, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.username = username;
        this.password = password;
        this.cf = codiceFiscale;

    }

    /**
     * Restituisce il nome utente.
     *
     * @return Il nome utente.
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Restituisce il cognome utente.
     *
     * @return Il cognome utente.
     */
    public String getCognome() {
        return this.cognome;
    }

    /**
     * Restutuisce il codiceFiscale dell'utente
     * 
     * @return il codice fiscale dell'utente
     */
    public String getCF() {
        return cf;
    }

    /**
     * Restituisce l'email utente.
     *
     * @return l'email utente.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Restituisce lo username dell'utente.
     *
     * @return Il lo username dell'utente.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Restituisce la password dell'utente.
     *
     * @return La password dell'utente.
     */
    public String getPassword() {
        return this.password;
    }

    
}
