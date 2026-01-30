//Giodi Carolo 758379
package client;

import java.net.*;
import java.io.*;
import java.util.*;

public class Proxy {
    private InetAddress ip;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * Costruttore della classe Proxy.
     * Inizializza un oggetto Proxy senza connessione.
     */
    public Proxy() {
    }

    /**
     * Stabilisce una connessione al server sulla porta 8080
     * e inizializza gli stream di input e output.
     */
    public synchronized void connect() {
        try {
            this.ip = InetAddress.getByName(null);
            socket = new Socket(ip, 8080);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
            notifyAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Attende finché la connessione e gli stream non sono pronti.
     */
    public synchronized void waitUntilConnected() {
        while (in == null || out == null) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Invia una lista di libri al server per aggiungerli.
     *
     * @param listaLibri Lista di libri da aggiungere.
     */
    public void aggiungiLibri(List<String[]> listaLibri) {
        try {
            out.writeObject("aggiungiLibri");
            out.flush();
            out.writeObject(listaLibri);
            out.flush();
            String risultato = (String) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Restituisce l'utente corrispondente a un codice fiscale.
     *
     * @param cf Codice fiscale dell'utente.
     * @return Oggetto Utente corrispondente, o null se non trovato.
     */
    public Utente getUtenteDaCF(String cf) {
        try {
            out.writeObject("getUtenteDaCF");
            out.flush();
            out.writeObject(cf);
            out.flush();
            return (Utente) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Effettua il login di un utente.
     *
     * @param username Username dell'utente.
     * @param password Password dell'utente.
     * @return true se il login è avvenuto con successo, false altrimenti.
     */
    public boolean login(String username, String password) {
        try {
            out.writeObject("login");
            out.flush();
            out.writeObject(username);
            out.flush();
            out.writeObject(password);
            out.flush();
            boolean b = (boolean) in.readObject();
            return b;
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Registra un nuovo utente nel server.
     *
     * @param u Utente da registrare.
     * @return true se la registrazione ha avuto successo, false altrimenti.
     */
    public boolean register(Utente u) {
        try {
            out.writeObject("register");
            out.flush();
            out.writeObject(u);
            out.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Restituisce il codice fiscale di un utente.
     *
     * @param username Username dell'utente.
     * @param password Password dell'utente.
     * @return Codice fiscale dell'utente, o stringa vuota se non trovato.
     */
    public String getCodiceFiscale(String username, String password) {
        String cf = "";
        try {
            out.writeObject("GetCodiceFiscale");
            out.flush();
            out.writeObject(username);
            out.flush();
            out.writeObject(password);
            out.flush();
            cf = (String) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
        }
        return cf;
    }

    /**
     * Ricerca libri in base al titolo.
     *
     * @param titolo Titolo del libro da cercare.
     * @return Lista di libri corrispondenti.
     */
    public ArrayList<Libro> ricercaPerTitolo(String titolo) {
        ArrayList<Libro> listaLibri = new ArrayList<>();
        try {
            out.writeObject("RicercaPerTitolo");
            out.flush();
            out.writeObject(titolo);
            out.flush();
            listaLibri = (ArrayList<Libro>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return listaLibri;
        }
        return listaLibri;
    }

    /**
     * Ricerca libri in base all'autore.
     *
     * @param autore Autore del libro da cercare.
     * @return Lista di libri corrispondenti.
     */
    public ArrayList<Libro> ricercaPerAutore(String autore) {
        ArrayList<Libro> listaLibri = new ArrayList<>();
        try {
            out.writeObject("RicercaPerAutore");
            out.flush();
            out.writeObject(autore);
            out.flush();
            listaLibri = (ArrayList<Libro>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return listaLibri;
        }
        return listaLibri;
    }

    /**
     * Ricerca libri in base a autore e anno.
     *
     * @param autore Autore del libro.
     * @param anno   Anno di pubblicazione.
     * @return Lista di libri corrispondenti.
     */
    public ArrayList<Libro> ricercaPerAutoreEAnno(String autore, int anno) {
        ArrayList<Libro> listaLibri = new ArrayList<>();
        try {
            out.writeObject("CercaPerAutoreEAnno");
            out.flush();
            out.writeObject(autore);
            out.flush();
            out.writeObject(anno);
            out.flush();
            listaLibri = (ArrayList<Libro>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return listaLibri;
        }
        return listaLibri;
    }

    /**
     * Aggiunge una libreria per l'utente.
     *
     * @param libreria Libreria da aggiungere.
     * @return true se l'aggiunta ha avuto successo, false altrimenti.
     */
    public boolean aggiungiLibreria(Librerie libreria) {
        try {
            out.writeObject("AggiungiLibreria");
            out.flush();
            out.writeObject(libreria);
            out.flush();
            boolean b = (boolean) in.readObject();
            return b;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Aggiunge una valutazione di un libro.
     *
     * @param valLibro Valutazione da aggiungere.
     */
    public void aggiungiValutazione(ValutazioniLibro valLibro) {
        try {
            out.writeObject("AggiungiValutazione");
            out.flush();
            out.writeObject(valLibro);
            out.flush();
        } catch (IOException e) {
        }
    }

    /**
     * Restituisce la valutazione di un libro per un determinato utente.
     *
     * @param utente Utente che ha valutato il libro.
     * @param libro  Libro valutato.
     * @return ValutazioneLibro corrispondente, o null se non esiste.
     */
    public ValutazioniLibro getValutazioniLibro(Utente utente, Libro libro) {
        try {
            out.writeObject("GetValutazioneLibro");
            out.flush();
            out.writeObject(utente);
            out.flush();
            out.writeObject(libro);
            out.flush();
            ValutazioniLibro valutazioneLibro = (ValutazioniLibro) in.readObject();
            return valutazioneLibro;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Restituisce i suggerimenti di un libro dati da un utente.
     *
     * @param utente Utente che ha dato i suggerimenti.
     * @param libro  Libro per cui sono stati dati i suggerimenti.
     * @return SuggerimentiLibro corrispondente, o null se non esiste.
     */
    public SuggerimentiLibro getSuggerimentiLibro(Utente utente, Libro libro) {
        try {
            out.writeObject("getSuggerimentiLibro");
            out.flush();
            out.writeObject(utente);
            out.flush();
            out.writeObject(libro);
            out.flush();
            SuggerimentiLibro suggerimentiLibro = (SuggerimentiLibro) in.readObject();
            return suggerimentiLibro;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Controlla se esiste una valutazione di un libro per un utente.
     *
     * @param u Utente da controllare.
     * @param l Libro da controllare.
     * @return true se esiste, false altrimenti.
     */
    public boolean esisteValutazioneLibro(Utente u, Libro l) {
        try {
            out.writeObject("EsisteValutazioneLibro");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(l);
            out.flush();
            boolean esiste = (boolean) in.readObject();
            return esiste;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Restituisce tutte le librerie di un utente.
     *
     * @param utente Utente di cui ottenere le librerie.
     * @return Lista di librerie, o null se non disponibile.
     */
    public ArrayList<Librerie> getLibrerieUtente(Utente utente) {
        try {
            out.writeObject("getLibrerieUtente");
            out.flush();
            out.writeObject(utente);
            out.flush();
            ArrayList<Librerie> listaLibrerie = (ArrayList<Librerie>) in.readObject();
            return listaLibrerie;
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Elimina un libro da una libreria di un utente.
     *
     * @param u   Utente proprietario della libreria.
     * @param lib Libreria da cui rimuovere il libro.
     * @param l   Libro da eliminare.
     */
    public void eliminaLibroLibreriaUtente(Utente u, Librerie lib, Libro l) {
        try {
            out.writeObject("eliminaLibroLibreriaUtente");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(lib);
            out.flush();
            out.writeObject(l);
            out.flush();
        } catch (IOException e) {
        }
    }

    /**
     * Elimina una libreria di un utente.
     *
     * @param u   Utente proprietario della libreria.
     * @param lib Libreria da eliminare.
     */
    public void eliminaLibreria(Utente u, Librerie lib) {
        try {
            out.writeObject("eliminaLibreria");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(lib);
            out.flush();
        } catch (IOException e) {
        }
    }

    /**
     * Modifica un suggerimento di libro.
     *
     * @param sl Suggerimento da modificare.
     */
    public void modificaLibroSuggerito(SuggerimentiLibro sl) {
        try {
            out.writeObject("modificaLibroSuggerito");
            out.flush();
            out.writeObject(sl);
            out.flush();
        } catch (IOException e) {
        }
    }

    /**
     * Controlla se esiste un suggerimento di libro da un utente.
     *
     * @param u Utente che ha fatto il suggerimento.
     * @param l Libro per cui è stato fatto il suggerimento.
     * @return true se esiste, false altrimenti.
     */
    public boolean esisteSuggerimentoLibro(Utente u, Libro l) {
        try {
            out.writeObject("esisteSuggerimentoLibro");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(l);
            out.flush();
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Aggiunge un suggerimento di libro.
     *
     * @param sl Suggerimento da aggiungere.
     */
    public void aggiungiSuggerimenti(SuggerimentiLibro sl) {
        try {
            out.writeObject("aggiungiSuggerimenti");
            out.flush();
            out.writeObject(sl);
            out.flush();
        } catch (IOException e) {
        }
    }

    /**
     * Aggiunge un libro a una libreria.
     *
     * @param lib Libreria a cui aggiungere il libro.
     * @param l   Libro da aggiungere.
     */
    public void aggiungiLibroLibreria(Librerie lib, Libro l) {
        try {
            out.writeObject("aggiungiLibroLibreria");
            out.flush();
            out.writeObject(lib);
            out.flush();
            out.writeObject(l);
            out.flush();
        } catch (IOException e) {
        }
    }

    /**
     * Restituisce tutti i libri presenti sul server.
     *
     * @return Lista di libri, o null se non disponibile.
     */
    public ArrayList<Libro> getLibri() {
        try {
            out.writeObject("getLibri");
            out.flush();
            return (ArrayList<Libro>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Cerca libri in base a titolo, autore e anno.
     *
     * @param titolo Titolo del libro.
     * @param autore Autore del libro.
     * @param anno   Anno di pubblicazione.
     * @return Lista di libri corrispondenti.
     */
    public ArrayList<Libro> cercaLibri(String titolo, String autore, String anno) {
        try {
            out.writeObject("cercaLibri");
            out.flush();
            out.writeObject(titolo);
            out.flush();
            out.writeObject(autore);
            out.flush();
            out.writeObject(anno);
            out.flush();
            return (ArrayList<Libro>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return (new ArrayList<Libro>());
        }
    }

    /**
     * Controlla se una libreria esiste per un utente.
     *
     * @param u    Utente proprietario.
     * @param nome Nome della libreria.
     * @return true se esiste, false altrimenti.
     */
    public boolean esisteLibreriaUtente(Utente u, String nome) {
        try {
            out.writeObject("esisteLibreriaUtente");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(nome);
            out.flush();
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return true;
        }
    }

    /**
     * Controlla se un libro esiste in una libreria.
     *
     * @param lib Libreria da controllare.
     * @param l   Libro da cercare.
     * @return true se esiste, false altrimenti.
     */
    public boolean esisteLibroLibreria(Librerie lib, Libro l) {
        try {
            out.writeObject("esisteLibroLibreria");
            out.flush();
            out.writeObject(lib);
            out.flush();
            out.writeObject(l);
            out.flush();
            return (boolean) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return true;
        }
    }

    /**
     * Inserisce una valutazione di un libro.
     *
     * @param vl Valutazione da inserire.
     */
    public void inserisciValutazione(ValutazioniLibro vl) {
        try {
            out.writeObject("inserisciValutazione");
            out.flush();
            out.writeObject(vl);
            out.flush();
        } catch (IOException e) {
        }
    }

    /**
     * Restituisce gli utenti che hanno suggerito un libro.
     *
     * @param l Libro di riferimento.
     * @return Lista di utenti.
     */
    public ArrayList<Utente> getUtentiSuggerimenti(Libro l) {
        try {
            out.writeObject("getUtentiSuggerimenti");
            out.flush();
            out.writeObject(l);
            out.flush();
            return (ArrayList<Utente>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return (new ArrayList<Utente>());
        }
    }

    /**
     * Restituisce gli utenti che hanno valutato un libro.
     *
     * @param l Libro di riferimento.
     * @return Lista di utenti.
     */
    public ArrayList<Utente> getUtentiValutazioni(Libro l) {
        try {
            out.writeObject("getUtentiValutazioni");
            out.flush();
            out.writeObject(l);
            out.flush();
            return (ArrayList<Utente>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return (new ArrayList<Utente>());
        }
    }

    /**
     * Restituisce la media delle valutazioni di un libro.
     *
     * @param l Libro di riferimento.
     * @return Lista di valori medi delle valutazioni.
     */
    public ArrayList<Double> getMediaValutazione(Libro l) {
        try {
            out.writeObject("getMediaValutazione");
            out.flush();
            out.writeObject(l);
            out.flush();
            return (ArrayList<Double>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return (new ArrayList<Double>());
        }
    }
    public ArrayList<Utente> getTuttiUtenti(){
        try{
            out.writeObject("getTuttiUtenti");
            out.flush();
            return (ArrayList<Utente>)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return (new ArrayList<Utente>());
        }
    }
}
