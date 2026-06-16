package client;
import java.net.*;
import java.io.*;
import java.util.*;
public class Proxy{
    private InetAddress ip;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    public Proxy(){
    }
    public synchronized void connect(){
        try{
            this.ip = InetAddress.getByName(null);
            socket = new Socket(ip,8080);
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
            notifyAll();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public synchronized void waitUntilConnected() {
        while (in == null || out == null) {
            try {
                wait(); 
            } catch (InterruptedException ignored) {}
        }
    }
    public void aggiungiLibri(List<String[]> listaLibri){ //funziona
        try{
            out.writeObject("aggiungiLibri");
            out.flush();
            out.writeObject(listaLibri);
            out.flush();
            String risultato = (String)in.readObject();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public Utente getUtenteDaCF(String cf){
        try{
            out.writeObject("getUtenteDaCF");
            out.flush();
            out.writeObject(cf);
            out.flush();
            return (Utente)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return null;
        }
    }
    public boolean login(String username, String password){ //funziona
        try{
            out.writeObject("login");
            out.flush();
            out.writeObject(username);
            out.flush();
            out.writeObject(password);
            out.flush();
            boolean b= (boolean)in.readObject();
            return b;
        }catch(Exception e){}
        return false;
    }
    
    public boolean register(Utente u){ //register
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

    public String getCodiceFiscale(String username,String password){ //funziona
        String cf = "";
        try {
            out.writeObject("GetCodiceFiscale");
            out.flush();
            out.writeObject(username);
            out.flush();
            out.writeObject(password);
            out.flush();
            cf = (String)in.readObject();
        } catch (IOException | ClassNotFoundException e) {}
        return cf;
    }
    public ArrayList<Libro> ricercaPerTitolo(String titolo){  //funziona
        ArrayList<Libro> listaLibri = new ArrayList<>();
        try {
            out.writeObject("RicercaPerTitolo");
            out.flush();
            out.writeObject(titolo);
            out.flush();
            listaLibri = (ArrayList<Libro>)in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return listaLibri;
        }
        return listaLibri;    
    }
    public ArrayList<Libro> ricercaPerAutore(String autore){ //funziona
        ArrayList<Libro> listaLibri = new ArrayList<>();
        try {
            out.writeObject("RicercaPerAutore");
            out.flush();
            out.writeObject(autore);
            out.flush();
            listaLibri = (ArrayList<Libro>)in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return listaLibri;
        }
        return listaLibri;
    }
    public ArrayList<Libro> ricercaPerAutoreEAnno(String autore, int anno){ //funziona
        ArrayList<Libro> listaLibri = new ArrayList<>();
        try {
            out.writeObject("CercaPerAutoreEAnno");
            out.flush();
            out.writeObject(autore);
            out.flush();
            out.writeObject(anno);
            out.flush();
            listaLibri = (ArrayList<Libro>)in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return listaLibri;
        }
        return listaLibri;
    }
    public boolean aggiungiLibreria(Librerie libreria){ //funziona
        try {
            out.writeObject("AggiungiLibreria");
            out.flush();
            out.writeObject(libreria);
            out.flush();
            boolean b = (boolean)in.readObject();
            return b;
        } catch (IOException | ClassNotFoundException e) {
            return false;
        }
    }
    public void aggiungiValutazione(ValutazioniLibro valLibro){ //funziona
        try {
            out.writeObject("AggiungiValutazione");
            out.flush();
            out.writeObject(valLibro);
            out.flush();
        } catch (IOException e) {
        }
    }
    public ValutazioniLibro getValutazioniLibro(Utente utente, Libro libro){ //funziona
        try{
            out.writeObject("GetValutazioneLibro");
            out.flush();
            out.writeObject(utente);
            out.flush();
            out.writeObject(libro);
            out.flush();
            ValutazioniLibro valutazioneLibro = (ValutazioniLibro)in.readObject();
            return valutazioneLibro;
        }catch(IOException | ClassNotFoundException e){
            return null;
        }
    }
    public SuggerimentiLibro getSuggerimentiLibro(Utente utente, Libro libro){  //funziona
        try{
            out.writeObject("getSuggerimentiLibro");
            out.flush();
            out.writeObject(utente);
            out.flush();
            out.writeObject(libro);
            out.flush();
            SuggerimentiLibro suggerimentiLibro = (SuggerimentiLibro)in.readObject();
            return suggerimentiLibro;
        }catch(IOException | ClassNotFoundException e){
            return null;
        }
    }
    public boolean esisteValutazioneLibro(Utente u, Libro l){ //funziona
        try{
            out.writeObject("EsisteValutazioneLibro");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(l);
            out.flush();
            boolean esiste = (boolean)in.readObject();
            return esiste;
        }catch(IOException | ClassNotFoundException e){
            return false;

        }
    }
    public ArrayList<Librerie> getLibrerieUtente(Utente utente){ //funziona
        try{
            out.writeObject("getLibrerieUtente");
            out.flush();
            out.writeObject(utente);
            out.flush();
            ArrayList<Librerie> listaLibrerie = (ArrayList<Librerie>)in.readObject();
            return listaLibrerie;
        }catch(IOException | ClassNotFoundException e){
            return null;
        }
    }
    public void eliminaLibroLibreriaUtente(Utente u,Librerie lib, Libro l){//funziona
        try{
            out.writeObject("eliminaLibroLibreriaUtente");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(lib);
            out.flush();
            out.writeObject(l);
            out.flush();
        }catch(IOException e){}
    }
    public void eliminaLibreria(Utente u,Librerie lib){ //funziona
        try{
            out.writeObject("eliminaLibreria");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(lib);
            out.flush();
        }catch(IOException e){}
    }
    public void modificaLibroSuggerito(SuggerimentiLibro sl){ //funziona
        try{
            out.writeObject("modificaLibroSuggerito");
            out.flush();
            out.writeObject(sl);
            out.flush();
        }catch(IOException e){}
    }
    public boolean esisteSuggerimentoLibro(Utente u, Libro l){ //funziona
        try{
            out.writeObject("esisteSuggerimentoLibro");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(l);
            out.flush();
            return (boolean)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return false;
        }
    }
    public void aggiungiSuggerimenti(SuggerimentiLibro sl){ //funziona
        try{
            out.writeObject("aggiungiSuggerimenti");
            out.flush();
            out.writeObject(sl);
            out.flush();
        }catch(IOException e){}
    }
    public void aggiungiLibroLibreria(Librerie lib, Libro l){ //funziona
        try{
            out.writeObject("aggiungiLibroLibreria");
            out.flush();
            out.writeObject(lib);
            out.flush();
            out.writeObject(l);
            out.flush();
        }catch(IOException e){}
    }
    public ArrayList<Libro> getLibri(){
        try{
            out.writeObject("getLibri");
            out.flush();
            return (ArrayList<Libro>)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return null;
        }
    }
    public ArrayList<Libro> cercaLibri(String titolo,String autore,String anno){
        try{
            out.writeObject("cercaLibri");
            out.flush();
            out.writeObject(titolo);
            out.flush();
            out.writeObject(autore);
            out.flush();
            out.writeObject(anno);
            out.flush();
            return (ArrayList<Libro>)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return (new ArrayList<Libro>());
        }
    }
    public boolean esisteLibreriaUtente(Utente u,String nome){
        try{
            out.writeObject("esisteLibreriaUtente");
            out.flush();
            out.writeObject(u);
            out.flush();
            out.writeObject(nome);
            out.flush();
            return (boolean)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return true;
        }
    }
    public boolean esisteLibroLibreria(Librerie lib, Libro l){
        try{
            out.writeObject("esisteLibroLibreria");
            out.flush();
            out.writeObject(lib);
            out.flush();
            out.writeObject(l);
            out.flush();
            return (boolean)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return true;
        }
    }
    public void inserisciValutazione(ValutazioniLibro vl){
        try{
            out.writeObject("inserisciValutazione");
            out.flush();
            out.writeObject(vl);
            out.flush();
        }catch(IOException e){}
    }
    public ArrayList<Utente> getUtentiSuggerimenti(Libro l){
        try{
            out.writeObject("getUtentiSuggerimenti");
            out.flush();
            out.writeObject(l);
            out.flush();
            return (ArrayList<Utente>)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return (new ArrayList<Utente>());
        }
    }
    public ArrayList<Utente> getUtentiValutazioni(Libro l){
        try{
            out.writeObject("getUtentiValutazioni");
            out.flush();
            out.writeObject(l);
            out.flush();
            return (ArrayList<Utente>)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return (new ArrayList<Utente>());
        }
    }
    public ArrayList<Integer> getMediaValutazione(Libro l){
        try{
            out.writeObject("getMediaValutazione");
            out.flush();
            out.writeObject(l);
            out.flush();
            return (ArrayList<Integer>)in.readObject();
        }catch(IOException | ClassNotFoundException e){
            return (new ArrayList<Integer>());
        }
    }
    

    
}