//Giodi Carolo 758379
package server;
import java.net.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import client.Librerie;
import client.Libro;
import client.SuggerimentiLibro;
import client.ValutazioniLibro;
import client.Utente;
public class ServerMulti extends Thread{
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Connection connection;
    private Statement stmt;
    public ServerMulti(Socket socket){
        this.socket = socket;
        try {
           connection = DriverManager.getConnection(
   "jdbc:postgresql://aws-1-eu-west-1.pooler.supabase.com:6543/postgres",
   "postgres.fmxyyrxfvnmetgtcsgnm",
   "Laboratoriab"
);
            this.stmt = connection.createStatement();
            this.out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            this.in = new ObjectInputStream(socket.getInputStream());
            this.start();
        } catch (SQLException | IOException e) {    
            e.printStackTrace();
        }
    }
    public void run(){
        try{
            while(!socket.isClosed()){
                String operazione = (String)in.readObject();
                System.out.println("Operazione: "+operazione);
                if(operazione.equals("Fine")){
                    System.out.println("Chiusura connessione in corso...");
                    socket.close();
                    connection.close();
                }
                else if(operazione.equals("aggiungiLibri")){
                    List<String[]> listaLibri = (List<String[]>)in.readObject();
                    System.out.println(listaLibri.size());
                    for(int i=1; i<listaLibri.size();i++){
                        String titolo = listaLibri.get(i)[0];
                        String autore = listaLibri.get(i)[1];
                        if(titolo.length()>=100){
                            titolo = titolo.substring(0,100);
                        }
                        if(autore.length()>=100){
                            autore = autore.substring(0,100);
                        }
                        int anno = Integer.parseInt(listaLibri.get(i)[2]);
                        String query = "INSERT INTO libro(titolo,autore,anno_pubblicazione) VALUES(?,?,?)";
                        PreparedStatement ps = connection.prepareStatement(query);
                        ps.setString(1,titolo);
                        ps.setString(2,autore);
                        ps.setInt(3,anno);
                        ps.executeUpdate(); 
                    }
                    out.writeObject("Ok");
                    out.flush();
                }
                else if(operazione.equals("login")){
                    String username = (String)in.readObject();
                    String password = (String)in.readObject();
                    boolean b = false;
                    String query = "SELECT * FROM utente WHERE username = ? AND password = ?";
                    PreparedStatement ps = connection.prepareStatement(query);
                    ps.setString(1,username);
                    ps.setString(2,password);
                    ResultSet risultato = ps.executeQuery();
                    if (risultato.next()) {
                        b=true;
                    } else {
                        b=false;
                        }
                    out.writeObject(b);
                    out.flush();
                }
                else if(operazione.equals("GetUtenteDaCF")){
                    String codiceFiscale = (String)in.readObject();
                    String comandoQuery = "SELECT nome, cognome, email, codifiscale, password, username FROM utente WHERE codicefiscale = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,codiceFiscale);
                    ResultSet rs = ps.executeQuery();
                    Utente utente = null;
                    while(rs.next()){
                        utente= new Utente(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6));
                    }
                    out.writeObject(utente);
                    out.flush();
                }
                else if(operazione.equals("register")){
                        Utente utente = (Utente)in.readObject();
                        String comandoQuery = "INSERT INTO utente(nome,cognome,email,codicefiscale,password,username) VALUES(?,?,?,?,?,?)";
                        PreparedStatement ps = connection.prepareStatement(comandoQuery);
                        ps.setString(1,utente.getNome());
                        ps.setString(2,utente.getCognome());
                        ps.setString(3,utente.getEmail());
                        ps.setString(4,utente.getCF());
                        ps.setString(5,utente.getPassword());
                        ps.setString(6,utente.getUsername());
                        ps.executeUpdate();
                }
                else if(operazione.equals("GetCodiceFiscale")){
                    String username = (String)in.readObject();
                    String password = (String)in.readObject();
                    String sqlCommand = "SELECT codicefiscale FROM utente WHERE username=? and password=?";
                    PreparedStatement ps = connection.prepareStatement(sqlCommand);
                    ps.setString(1,username);
                    ps.setString(2,password);
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){
                        String cf = rs.getString(1);
                        out.writeObject(cf);
                        out.flush();
                    }else{
                        out.writeObject("");
                        out.flush();
                    }
                }
                else if(operazione.equals("RicercaPerTitolo")){
                    String titolo= ((String)in.readObject()).toLowerCase();
                    ArrayList<Libro> listaLibri = new ArrayList<>();
                    String comandoQuery = "SELECT titolo, autore, anno_pubblicazione FROM libro WHERE LOWER(titolo) LIKE ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1, "%"+titolo+"%");
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()){
                        listaLibri.add(new Libro(rs.getString(1),rs.getString(2),rs.getInt(3)));
                    }
                    out.writeObject(listaLibri);
                    out.flush();
                }
                else if(operazione.equals("RicercaPerAutore")){
                    ArrayList<Libro> alLibro = new ArrayList();
                    String autore = (String)in.readObject();
                    autore = autore.toLowerCase();
                    String comandoQuery = "SELECT titolo, autore, anno_pubblicazione FROM libro WHERE LOWER(autore) LIKE ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,"%"+autore+"%");
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()){
                        alLibro.add(new Libro(rs.getString(1),rs.getString(2),rs.getInt(3)));
                    }
                    out.writeObject(alLibro);
                    out.flush();    
                }
                else if(operazione.equals("CercaPerAutoreEAnno")){
                    ArrayList<Libro> alLibro = new ArrayList();
                    String autore = (String)in.readObject();
                    autore = autore.toLowerCase();
                    int anno = (int)in.readObject();
                    String comandoQuery = "SELECT titolo, autore, anno_pubblicazione FROM libro WHERE LOWER(autore) LIKE ? and anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,"%"+autore+"%");
                    ps.setInt(2,anno);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()){   
                        alLibro.add(new Libro(rs.getString(1),rs.getString(2),rs.getInt(3)));
                    }
                    out.writeObject(alLibro);
                    out.flush();
                }
                else if(operazione.equals("GetValutazioneLibro")){ 
                    Utente u = (Utente)in.readObject();
                    String cf = u.getCF();
                    Libro libro = (Libro)in.readObject();
                    String comandoQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,libro.getTitolo());
                    ps.setString(2,libro.getAutore());
                    ps.setInt(3, libro.getAnnoPubblicazione());
                    ResultSet rs = ps.executeQuery();
                    int book_id = 0;
                    if(rs.next()){
                        book_id = rs.getInt(1);
                    }
                    comandoQuery = "SELECT v.* FROM valutazione v JOIN utente u on(v.codicefiscale = u.codicefiscale) JOIN libro l on(l.book_id = v.book_id) where v.codicefiscale = ? and v.book_id = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,cf);
                    ps.setInt(2,book_id);
                    rs = ps.executeQuery();
                    if(rs.next()){
                        int stile = rs.getInt("stile");
                        String noteStile = rs.getString("nota_stile");
                        int contenuto = rs.getInt("contenuto");
                        String noteContenuto = rs.getString("nota_contenuto");
                        int gradevolezza = rs.getInt("gradevolezza");
                        String noteGradevolezza = rs.getString("nota_gradevolezza");
                        int originalita = rs.getInt("originalita");
                        String noteOriginalita = rs.getString("nota_originalita");
                        int edizione = rs.getInt("edizione");
                        String noteEdizione = rs.getString("nota_edizione");
                        int votoFinale = rs.getInt("voto_finale");
                        String noteVotoFinale = rs.getString("nota_voto_finale");
                        ValutazioniLibro valutazioneLibro = new ValutazioniLibro(u,libro);
                        valutazioneLibro.inserisciValutazioneLibro(stile, noteStile, contenuto, noteContenuto, gradevolezza, noteGradevolezza, originalita, noteOriginalita, edizione, noteEdizione, votoFinale, noteVotoFinale);  
                        out.writeObject(valutazioneLibro);
                        out.flush(); 
                    }else{
                        out.writeObject(null);
                        out.flush();
                    }
                }
                else if(operazione.equals("getSuggerimentiLibro")){
                    Utente u = (Utente)in.readObject();
                    String cf = u.getCF();
                    Libro libro = (Libro)in.readObject();
                    String comandoQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,libro.getTitolo());
                    ps.setString(2,libro.getAutore());
                    ps.setInt(3, libro.getAnnoPubblicazione());
                    ResultSet rs = ps.executeQuery();
                    int book_id = 0;
                    if(rs.next()){
                        book_id = rs.getInt(1);
                    }
                    comandoQuery ="SELECT book FROM libriconsigliati WHERE codiceFiscale = ? AND book_recommended = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,cf);
                    ps.setInt(2,book_id);
                    rs = ps.executeQuery();
                    SuggerimentiLibro sl = new SuggerimentiLibro(libro, u);
                    ArrayList<Libro> lista = new ArrayList<>();
                    while(rs.next()){
                        String operazioneQuery = "SELECT titolo,autore,anno_pubblicazione FROM libro WHERE book_id = ?";
                        PreparedStatement pss = connection.prepareStatement(operazioneQuery);
                        pss.setInt(1,rs.getInt("book"));
                        ResultSet risultato = pss.executeQuery();
                        risultato.next();
                        lista.add(new Libro(risultato.getString("titolo"),risultato.getString("autore"),risultato.getInt("anno_pubblicazione")));
                    }
                    sl.inserisciSuggerimento(lista);
                    out.writeObject(sl);
                    out.flush();
                }
                 else if(operazione.equals("EsisteValutazioneLibro")){ //cambiare getidlibro
                    Utente u = (Utente)in.readObject();
                    Libro libro = (Libro)in.readObject();
                    String comandoQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,libro.getTitolo());
                    ps.setString(2,libro.getAutore());
                    ps.setInt(3, libro.getAnnoPubblicazione());
                    ResultSet rs = ps.executeQuery();
                    int book_id = 0;
                    if(rs.next()){
                        book_id = rs.getInt(1);
                    }
                    String cf = u.getCF();
                    comandoQuery = "SELECT rating_id FROM valutazione WHERE codicefiscale = ? and book_id = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,cf);
                    ps.setInt(2,book_id);
                    rs = ps.executeQuery();
                    if(rs.next()){
                        out.writeObject(true);
                        out.flush();
                    }else{
                        out.writeObject(false);
                        out.flush();
                    }
                }
                else if(operazione.equals("AggiungiLibreria")){ //DA CAMBIARE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    Librerie libreria = (Librerie)in.readObject();
                    String nomeLibreria = libreria.getNome();
                    String cf = libreria.getUtente().getCF();
                    //Controllo se esiste una libraria con questo nome di questo utente
                    String comandoQuery = "Select library_id FROM libreria WHERE codicefiscale = ? AND nome = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,cf);
                    ps.setString(2,nomeLibreria);
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){
                        out.writeObject(false);
                        out.flush();
                    }else{
                        comandoQuery = "INSERT INTO libreria(nome,codicefiscale) VALUES (?,?)";
                        ps = connection.prepareStatement(comandoQuery,Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, nomeLibreria);
                        ps.setString(2,cf);
                        ps.executeUpdate();
                        rs = ps.getGeneratedKeys();
                        rs.next();
                        int idLibreria = rs.getInt(1);
                        ArrayList<Libro> alLibro = libreria.getAlLibri();
                        comandoQuery = "INSERT INTO libro_libreria(library_id,book_id) VALUES (?,?)";
                        ps = connection.prepareStatement(comandoQuery);
                        String queryIdLibro = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                        PreparedStatement psIdLibro = connection.prepareStatement(queryIdLibro);
                        for(int i=0;i<alLibro.size();i++){
                            ps.setInt(1,idLibreria);
                            psIdLibro.setString(1,alLibro.get(i).getTitolo());
                            psIdLibro.setString(2,alLibro.get(i).getAutore());
                            psIdLibro.setInt(3,alLibro.get(i).getAnnoPubblicazione());
                            ResultSet risultato = psIdLibro.executeQuery();
                            risultato.next();
                            int idLibro = risultato.getInt(1);
                            ps.setInt(1,idLibreria);
                            ps.setInt(2,idLibro);
                            ps.executeUpdate();
                        }
                        out.writeObject(true);
                    }
                }
                else if(operazione.equals("getLibrerieUtente")){ 
                    ArrayList<Librerie> lib = new ArrayList<Librerie>();
                    Utente utente = (Utente)in.readObject();
                    String comandoQuery = "SELECT nome,library_id FROM libreria where codicefiscale = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,utente.getCF());
                    ResultSet risultato = ps.executeQuery();
                    while(risultato.next()){
                        System.out.println(risultato.getString("nome"));
                        String operazioneQuery = "SELECT titolo,autore,anno_pubblicazione FROM libro l join libro_libreria ll ON(l.book_id=ll.book_id) where library_id = ?";
                        PreparedStatement pss = connection.prepareStatement(operazioneQuery);
                        pss.setInt(1,risultato.getInt("library_id"));
                        ResultSet rs = pss.executeQuery();
                        Librerie libreria = new Librerie(risultato.getString("nome"),utente);
                        while(rs.next()){
                            libreria.aggiungiLibro(new Libro(rs.getString("titolo"),rs.getString("autore"),rs.getInt("anno_pubblicazione")));
                        }
                        lib.add(libreria);     
                    }
                    out.writeObject(lib);
                    out.flush();
                }
                else if(operazione.equals("AggiungiValutazione")){
                    ValutazioniLibro valutazioneLibro = (ValutazioniLibro)in.readObject();
                    //Controlla se esiste già una valutazione;
                    Utente u = valutazioneLibro.getUtente();
                    Libro l = valutazioneLibro.getLibro();
                    String cf = u.getCF();
                    String comandoQuery = "SELECT libro.book_id FROM valutazione JOIN libro ON(libro.book_id = valutazione.book_id) WHERE codicefiscale = ? AND titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,cf);
                    ps.setString(2,l.getTitolo());
                    ps.setString(3,l.getAutore());
                    ps.setInt(4,l.getAnnoPubblicazione());
                    ResultSet rs = ps.executeQuery();
                    //fine controllo
                    int stile = valutazioneLibro.getStile();
                    String noteStile = valutazioneLibro.getNoteStile();
                    int contenuto = valutazioneLibro.getContenuto();
                    String noteContenuto = valutazioneLibro.getNoteContenuto();
                    int gradevolezza = valutazioneLibro.getGradevolezza();
                    String noteGradevolezza = valutazioneLibro.getNoteGradevolezza();
                    int originalita = valutazioneLibro.getOriginalita();
                    String noteOriginalita = valutazioneLibro.getNoteOriginalita();
                    int edizione = valutazioneLibro.getEdizione();
                    String noteEdizione = valutazioneLibro.getNoteEdizione();
                    double votoFinale = valutazioneLibro.getMediaFinale();
                    String noteVotoFinale = valutazioneLibro.getNoteVotoFinale();
                    if(rs.next()){ //c'è gia una valutazione
                        comandoQuery = "UPDATE valutazione SET stile = ?,nota_stile = ?,contenuto = ?,nota_contenuto = ?,gradevolezza = ?,nota_gradevolezza = ?,originalita = ?,nota_originalita = ?,edizione = ?,nota_edizione = ?,voto_finale = ?,nota_voto_finale = ? WHERE codicefiscale = ? AND book_id = ?";
                        ps = connection.prepareStatement(comandoQuery);
                        ps.setInt(1,stile);
                        ps.setString(2,noteStile);
                        ps.setInt(3,contenuto);
                        ps.setString(4,noteContenuto);
                        ps.setInt(5,gradevolezza);
                        ps.setString(6,noteGradevolezza);
                        ps.setInt(7,originalita);
                        ps.setString(8,noteOriginalita);
                        ps.setInt(9,edizione);
                        ps.setString(10,noteEdizione);
                        ps.setDouble(11,votoFinale);
                        ps.setString(12,noteVotoFinale);
                        ps.setString(13,cf);
                        int idLibro = rs.getInt(1);
                        ps.setInt(14,idLibro);
                        ps.executeUpdate();
                    }else{
                        comandoQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                        ps = connection.prepareStatement(comandoQuery);
                        ps.setString(1,l.getTitolo());
                        ps.setString(2,l.getAutore());
                        ps.setInt(3,l.getAnnoPubblicazione());
                        rs = ps.executeQuery();
                        rs.next();
                        int idLibro = rs.getInt(1);
                        comandoQuery = "INSERT INTO valutazione(book_id,stile,nota_stile,contenuto,nota_contenuto,gradevolezza,nota_gradevolezza,originalita,nota_originalita,edizione,nota_edizione,voto_finale,nota_voto_finale,codicefiscale) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        ps = connection.prepareStatement(comandoQuery);
                        ps.setInt(1,idLibro);
                        ps.setInt(2,stile);
                        ps.setString(3,noteStile);
                        ps.setInt(4,contenuto);
                        ps.setString(5,noteContenuto);
                        ps.setInt(6,gradevolezza);
                        ps.setString(7,noteGradevolezza);
                        ps.setInt(8,originalita);
                        ps.setString(9,noteOriginalita);
                        ps.setInt(10,edizione);
                        ps.setString(11,noteEdizione);
                        ps.setDouble(12,votoFinale);
                        ps.setString(13,noteVotoFinale);
                        ps.setString(14,cf);
                        ps.executeUpdate();
                    }
                }else if(operazione.equals("eliminaLibroLibreriaUtente")){
                    Utente u =(Utente)in.readObject();
                    Librerie lib = (Librerie)in.readObject();
                    Libro l = (Libro)in.readObject();
                    String nome_lib = lib.getNome();
                    String codiceFiscale = u.getCF();

                    String comandoQuery = "SELECT library_id FROM libreria WHERE nome = ? AND codicefiscale = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,nome_lib);
                    ps.setString(2,codiceFiscale);
                    ResultSet risultato = ps.executeQuery();
                    risultato.next();
                    int ll_id = risultato.getInt("library_id");

                    //prendo id libro
                    String titolo = l.getTitolo();
                    String autore = l.getAutore();
                    int anno = l.getAnnoPubblicazione();
                    comandoQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,titolo);
                    ps.setString(2,autore);
                    ps.setInt(3,anno);
                    risultato = ps.executeQuery();
                    risultato.next();
                    int id_libro = risultato.getInt("book_id");

                    comandoQuery = "DELETE FROM libro_libreria WHERE library_id = ? AND book_id = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setInt(1,ll_id);
                    ps.setInt(2,id_libro);
                    ps.executeUpdate();
                }else if(operazione.equals("eliminaLibreria")){
                    Utente u=(Utente)in.readObject();
                    Librerie lib = (Librerie)in.readObject();
                    String nome_lib = lib.getNome();
                    String codiceFiscale = u.getCF();
                    String comandoQuery = "Select library_id FROM libreria WHERE nome = ? AND codicefiscale = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,nome_lib);
                    ps.setString(2,codiceFiscale);
                    ResultSet risultato = ps.executeQuery();
                    risultato.next();
                    int id = risultato.getInt("library_id");
                    comandoQuery = "DELETE FROM libro_libreria WHERE library_id = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setInt(1,id);
                    ps.executeUpdate();
                    comandoQuery = "DELETE FROM libreria WHERE nome = ? AND codicefiscale = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,nome_lib);
                    ps.setString(2,codiceFiscale);
                    ps.executeUpdate();
                }else if(operazione.equals("modificaLibroSuggerito")){
                    SuggerimentiLibro sl = (SuggerimentiLibro)in.readObject();
                    Utente u = sl.getUtente();
                    Libro riferimento = sl.getLibro();
                    ArrayList<Libro> lista = sl.getALLibri();
                    String codiceFiscale = u.getCF();
                    String titolo = riferimento.getTitolo();
                    String autore = riferimento.getAutore();
                    int anno = riferimento.getAnnoPubblicazione();
                    //prendo l'id del libro su cui faccio suggerimenti
                    ArrayList<Libro> suggerimentiAttuali = new ArrayList<>();
                    String comandoQuery = "SELECT book_id from libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,titolo);
                    ps.setString(2,autore);
                    ps.setInt(3,anno);
                    ResultSet risultato = ps.executeQuery();
                    risultato.next();
                    int id_book_recommended = risultato.getInt("book_id");
                    //cancello i nuovi libri per far spazio a quelli nuovi
                    comandoQuery = "DELETE FROM libriconsigliati WHERE book_recommended = ? and codicefiscale = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setInt(1,id_book_recommended);
                    ps.setString(2,codiceFiscale);
                    ps.executeUpdate();
                    
                    //prendo la lista di id dei nuovi libri
                    ArrayList<Integer> ids = new ArrayList<>();
                    comandoQuery = "SELECT book_id from libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    for(int i=0;i<lista.size();i++){
                        ps.setString(1,lista.get(i).getTitolo());
                        ps.setString(2,lista.get(i).getAutore());
                        ps.setInt(3,lista.get(i).getAnnoPubblicazione());
                        risultato = ps.executeQuery();
                        risultato.next();
                        ids.add(risultato.getInt("book_id"));
                    }
                    comandoQuery = "INSERT INTO libriconsigliati(codicefiscale,book,book_recommended) VALUES(?,?,?)";
                    ps = connection.prepareStatement(comandoQuery);
                    for(int i=0;i<ids.size();i++){
                        ps.setString(1,codiceFiscale);
                        ps.setInt(2,ids.get(i));
                        ps.setInt(3,id_book_recommended);
                        ps.executeUpdate();
                    }
                    
                }else if(operazione.equals("esisteSuggerimentoLibro")){
                    Utente u = (Utente)in.readObject();
                    Libro l = (Libro)in.readObject();
                    String comandoQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,l.getTitolo());
                    ps.setString(2,l.getAutore());
                    ps.setInt(3,l.getAnnoPubblicazione());
                    ResultSet risultato = ps.executeQuery();
                    risultato.next();
                    int idLibro = risultato.getInt("book_id");
                    comandoQuery = "SELECT * from libriconsigliati WHERE book_recommended = ? AND codicefiscale = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setInt(1,idLibro);
                    ps.setString(2,u.getCF());
                    risultato = ps.executeQuery();
                    if(risultato.next()){
                        out.writeObject(true);
                        out.flush();
                    }else{
                        out.writeObject(false);
                        out.flush();
                    }
                }else if(operazione.equals("aggiungiSuggerimenti")){
                    SuggerimentiLibro sl = (SuggerimentiLibro)in.readObject();
                    Utente u = sl.getUtente();
                    Libro l = sl.getLibro();
                    ArrayList<Libro> lista = sl.getALLibri();
                    String comandoQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,l.getTitolo());
                    ps.setString(2,l.getAutore());
                    ps.setInt(3,l.getAnnoPubblicazione());
                    ResultSet risultato = ps.executeQuery();
                    risultato.next();
                    int id_libro_sorgente = risultato.getInt("book_id");
                    ArrayList<Integer> ids = new ArrayList<>();
                    for(int i=0;i<lista.size();i++){
                        ps.setString(1,lista.get(i).getTitolo());
                        ps.setString(2,lista.get(i).getAutore());
                        ps.setInt(3,lista.get(i).getAnnoPubblicazione());
                        risultato = ps.executeQuery();
                        risultato.next();
                        ids.add(risultato.getInt("book_id"));
                    }
                    comandoQuery = "INSERT INTO libriconsigliati(codicefiscale,book,book_recommended) VALUES(?,?,?)";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,u.getCF());
                    ps.setInt(3,id_libro_sorgente);
                    for(int i=0;i<ids.size();i++){
                        ps.setInt(2,ids.get(i));
                        ps.executeUpdate();
                    }
                }else if(operazione.equals("aggiungiLibroLibreria")){
                    Librerie lib = (Librerie)in.readObject();
                    Libro l = (Libro)in.readObject();
                    //prendo id libreria
                    String comandoQuery = "SELECT library_id FROM libreria WHERE nome = ? AND codicefiscale = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,lib.getNome());
                    ps.setString(2,lib.getUtente().getCF());
                    ResultSet risultato = ps.executeQuery();
                    risultato.next();
                    int id_lib = risultato.getInt("library_id");

                    //prendo id libro
                    comandoQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,l.getTitolo());
                    ps.setString(2,l.getAutore());
                    ps.setInt(3,l.getAnnoPubblicazione());
                    risultato = ps.executeQuery();
                    risultato.next();
                    int id_libro = risultato.getInt("book_id");

                    comandoQuery = "INSERT INTO libro_libreria(book_id,library_id) VALUES(?,?)";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setInt(1,id_libro);
                    ps.setInt(2,id_lib);
                    ps.executeUpdate();
                }else if(operazione.equals("getUtenteDaCF")){
                    String cf = (String)in.readObject();
                    String comandoQuery = "SELECT * FROM utente WHERE codicefiscale = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,cf);
                    ResultSet risultato = ps.executeQuery();
                    risultato.next();
                    Utente u = new Utente(risultato.getString("nome"),risultato.getString("cognome"),risultato.getString("codicefiscale"),risultato.getString("email"),risultato.getString("password"),risultato.getString("username"));
                    out.writeObject(u);
                    out.flush();
                }else if(operazione.equals("getLibri")){
                    ArrayList<Libro> alLibri = new ArrayList<>();
                    String operazioneQuery = "SELECT * FROM libro";
                    PreparedStatement ps = connection.prepareStatement(operazioneQuery);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()){
                        alLibri.add(new Libro(rs.getString("titolo"),rs.getString("autore"),rs.getInt("anno_pubblicazione")));
                    }
                    out.writeObject(alLibri);
                }else if(operazione.equals("cercaLibri")){
                    String titolo = (String)in.readObject();
                    titolo = titolo.toLowerCase();
                    String autore = (String)in.readObject();
                    autore = autore.toLowerCase();
                    String anno = (String)in.readObject();
                    if(anno.length()!=0){
                        int annoP = Integer.parseInt(anno);
                        String operazioneQuery = "SELECT * FROM libro WHERE LOWER(titolo) like ? AND LOWER(autore) LIKE ? and anno_pubblicazione = ?";
                        PreparedStatement ps = connection.prepareStatement(operazioneQuery);
                        ps.setString(1,"%"+titolo+"%");
                        ps.setString(2,"%"+autore+"%");
                        ps.setInt(3,annoP);
                        ResultSet risultato = ps.executeQuery();
                        ArrayList<Libro> ricerca = new ArrayList<Libro>();
                        while(risultato.next()){
                            ricerca.add(new Libro(risultato.getString("titolo"),risultato.getString("autore"), risultato.getInt("anno_pubblicazione")));
                        }
                        out.writeObject(ricerca);
                        out.flush();
                    }else{
                        String operazioneQuery = "SELECT * FROM libro WHERE LOWER(titolo) like ? AND LOWER(autore) LIKE ?";
                        PreparedStatement ps = connection.prepareStatement(operazioneQuery);
                        ps.setString(1,"%"+titolo+"%");
                        ps.setString(2,"%"+autore+"%");
                        ResultSet risultato = ps.executeQuery();
                        ArrayList<Libro> ricerca = new ArrayList<Libro>();
                        while(risultato.next()){
                            ricerca.add(new Libro(risultato.getString("titolo"),risultato.getString("autore"), risultato.getInt("anno_pubblicazione")));
                        }
                        out.writeObject(ricerca);
                        out.flush();
                    }
                }else if(operazione.equals("esisteLibreriaUtente")){
                    Utente u = (Utente)in.readObject();
                    String nome = (String)in.readObject();
                    String operazioneQuery = "SELECT * FROM libreria WHERE codiceFiscale = ? and nome = ?";
                    PreparedStatement ps = connection.prepareStatement(operazioneQuery);
                    ps.setString(1,u.getCF());
                    ps.setString(2,nome);
                    ResultSet risultato = ps.executeQuery();
                    if(risultato.next()){
                        out.writeObject(true);
                        out.flush();
                    }else{
                        out.writeObject(false);
                        out.flush();
                    }
                }else if(operazione.equals("esisteLibroLibreria")){
                    Librerie lib = (Librerie)in.readObject();
                    Libro l = (Libro)in.readObject();
                    //prendo id libro
                    String comandoQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,l.getTitolo());
                    ps.setString(2,l.getAutore());
                    ps.setInt(3,l.getAnnoPubblicazione());
                    ResultSet risultato = ps.executeQuery();
                    risultato.next();
                    int id_libro = risultato.getInt("book_id");
                    comandoQuery = "SELECT library_id FROM libreria WHERE nome = ? AND codicefiscale = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,lib.getNome());
                    ps.setString(2,lib.getUtente().getCF());
                    risultato = ps.executeQuery();
                    risultato.next();
                    int id_libreria = risultato.getInt("library_id");
                    comandoQuery = "SELECT * FROM libro_libreria WHERE book_id = ? AND library_id = ?";
                    ps = connection.prepareStatement(comandoQuery);
                    ps.setInt(1,id_libro);
                    ps.setInt(2,id_libreria);
                    risultato = ps.executeQuery();
                    if(risultato.next()){
                        out.writeObject(true);
                        out.flush();
                    }else{
                        out.writeObject(false);
                        out.flush();
                    }
                }else if(operazione.equals("inserisciValutazione")){
                    ValutazioniLibro vl = (ValutazioniLibro)in.readObject();
                    //controllo se c'è ne gia una 
                    String comandoQuery = "SELECT rating_id FROM valutazione v join libro l on(v.book_id = l.book_id) WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ? AND codicefiscale = ?";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ps.setString(1,vl.getLibro().getTitolo());
                    ps.setString(2,vl.getLibro().getAutore()); 
                    ps.setInt(3,vl.getLibro().getAnnoPubblicazione());
                    ps.setString(4,vl.getUtente().getCF());
                    ResultSet risultato = ps.executeQuery();
                    if(!risultato.next()){
                        comandoQuery = "SELECT book_id FROM libro where titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                        ps = connection.prepareStatement(comandoQuery);
                        ps.setString(1,vl.getLibro().getTitolo());
                        ps.setString(2,vl.getLibro().getAutore()); 
                        ps.setInt(3,vl.getLibro().getAnnoPubblicazione());
                        risultato = ps.executeQuery();
                        risultato.next();
                        int id_libro = risultato.getInt("book_id");
                        comandoQuery = "INSERT INTO valutazione (stile,nota_stile,contenuto,nota_contenuto,gradevolezza,nota_gradevolezza,originalita,nota_originalita,edizione,nota_edizione,voto_finale,nota_voto_finale,book_id,codicefiscale) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        ps = connection.prepareStatement(comandoQuery);
                        ps.setInt(1,vl.getStile());
                        ps.setString(2,vl.getNoteStile());
                        ps.setInt(3,vl.getContenuto());
                        ps.setString(4,vl.getNoteContenuto());
                        ps.setInt(5,vl.getGradevolezza());
                        ps.setString(6,vl.getNoteGradevolezza());
                        ps.setInt(7,vl.getOriginalita());
                        ps.setString(8,vl.getNoteOriginalita());
                        ps.setInt(9,vl.getEdizione());
                        ps.setString(10,vl.getNoteEdizione());
                        ps.setDouble(11,vl.getMediaFinale());
                        ps.setString(12,vl.getNoteVotoFinale());
                        ps.setInt(13,id_libro);
                        ps.setString(14,vl.getUtente().getCF());
                        ps.executeUpdate();
                    }else{
                        comandoQuery = "SELECT book_id FROM libro where titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                        ps = connection.prepareStatement(comandoQuery);
                        ps.setString(1,vl.getLibro().getTitolo());
                        ps.setString(2,vl.getLibro().getAutore()); 
                        ps.setInt(3,vl.getLibro().getAnnoPubblicazione());
                        risultato = ps.executeQuery();
                        risultato.next();
                        int id_libro = risultato.getInt("book_id");
                        comandoQuery = "UPDATE valutazione SET stile = ?,nota_stile = ?,contenuto = ?,nota_contenuto = ?,gradevolezza = ?,nota_gradevolezza = ?,originalita = ?,nota_originalita = ?,edizione = ?,nota_edizione = ?,voto_finale = ?,nota_voto_finale = ? WHERE book_id = ? AND codicefiscale = ?";
                        ps.setInt(1,vl.getStile());
                        ps.setString(2,vl.getNoteStile());
                        ps.setInt(3,vl.getContenuto());
                        ps.setString(4,vl.getNoteContenuto());
                        ps.setInt(5,vl.getGradevolezza());
                        ps.setString(6,vl.getNoteGradevolezza());
                        ps.setInt(7,vl.getOriginalita());
                        ps.setString(8,vl.getNoteOriginalita());
                        ps.setInt(9,vl.getEdizione());
                        ps.setString(10,vl.getNoteEdizione());
                        ps.setDouble(11,vl.getMediaFinale());
                        ps.setString(12,vl.getNoteVotoFinale());
                        ps.setInt(13,id_libro);
                        ps.setString(14,vl.getUtente().getCF());
                        ps.executeUpdate();
                    }
                
                }else if(operazione.equals("getUtentiSuggerimenti")){
                    Libro l = (Libro)in.readObject();
                    String operazioneQuery ="SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(operazioneQuery);
                    ps.setString(1,l.getTitolo());
                    ps.setString(2,l.getAutore());
                    ps.setInt(3,l.getAnnoPubblicazione());
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    int id_libro = rs.getInt("book_id");

                    operazioneQuery = "SELECT DISTINCT nome,cognome,email,u.codicefiscale,username,password FROM utente u join libriconsigliati lc ON(u.codicefiscale = lc.codicefiscale) where book_recommended = ?";
                    ps = connection.prepareStatement(operazioneQuery);
                    ps.setInt(1,id_libro);
                    rs = ps.executeQuery();
                    ArrayList<Utente> alUtente = new ArrayList<>();
                    while(rs.next()){
                        alUtente.add(new Utente(rs.getString("nome"),rs.getString("cognome"),rs.getString("codicefiscale"),rs.getString("email"),rs.getString("username"),rs.getString("password")));
                    }
                    out.writeObject(alUtente);
                    out.flush();
                }else if(operazione.equals("getUtentiValutazioni")){
                    Libro l = (Libro)in.readObject();
                    String operazioneQuery ="SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(operazioneQuery);
                    ps.setString(1,l.getTitolo());
                    ps.setString(2,l.getAutore());
                    ps.setInt(3,l.getAnnoPubblicazione());
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    int id_libro = rs.getInt("book_id");

                    operazioneQuery = "SELECT DISTINCT nome,cognome,email,u.codicefiscale,username,password FROM utente u join valutazione v ON(u.codicefiscale = v.codicefiscale) where book_id = ?";
                    ps = connection.prepareStatement(operazioneQuery);
                    ps.setInt(1,id_libro);
                    rs = ps.executeQuery();
                    ArrayList<Utente> alUtente = new ArrayList<>();
                    while(rs.next()){
                        alUtente.add(new Utente(rs.getString("nome"),rs.getString("cognome"),rs.getString("codicefiscale"),rs.getString("email"),rs.getString("username"),rs.getString("password")));
                    }
                    out.writeObject(alUtente);
                    out.flush();
                } else if (operazione.equals("getMediaValutazione")) {
                    Libro l = (Libro) in.readObject();

                    // Prendo l'id del libro
                    String operazioneQuery = "SELECT book_id FROM libro WHERE titolo = ? AND autore = ? AND anno_pubblicazione = ?";
                    PreparedStatement ps = connection.prepareStatement(operazioneQuery);
                    ps.setString(1, l.getTitolo());
                    ps.setString(2, l.getAutore());
                    ps.setInt(3, l.getAnnoPubblicazione());
                    ResultSet rs = ps.executeQuery();

                    int id_libro = 0;
                    if (rs.next()) {
                        id_libro = rs.getInt("book_id");
                    }

                    // Media dei voti principali + calcolo del voto finale lato server
                    operazioneQuery = "SELECT " +
                            "COALESCE(avg(stile),0), " +
                            "COALESCE(avg(contenuto),0), " +
                            "COALESCE(avg(gradevolezza),0), " +
                            "COALESCE(avg(originalita),0), " +
                            "COALESCE(avg(edizione),0) " +
                            "FROM valutazione WHERE book_id = ?";

                    ps = connection.prepareStatement(operazioneQuery);
                    ps.setInt(1, id_libro);
                    rs = ps.executeQuery();

                    ArrayList<Double> valutazioni = new ArrayList<>();
                    if (rs.next()) {
                        double stile = rs.getDouble(1);
                        double contenuto = rs.getDouble(2);
                        double gradevolezza = rs.getDouble(3);
                        double originalita = rs.getDouble(4);
                        double edizione = rs.getDouble(5);

                        valutazioni.add(stile);
                        valutazioni.add(contenuto);
                        valutazioni.add(gradevolezza);
                        valutazioni.add(originalita);
                        valutazioni.add(edizione);

                        double votoFinale = (stile + contenuto + gradevolezza + originalita + edizione) / 5.0;
                        valutazioni.add(votoFinale);
                    }

                    out.writeObject(valutazioni);
                    out.flush();
                }else if(operazione.equals("getTuttiUtenti")){
                    String comandoQuery = "SELECT * FROM utente";
                    PreparedStatement ps = connection.prepareStatement(comandoQuery);
                    ResultSet rs = ps.executeQuery();
                    ArrayList<Utente> alUtente = new ArrayList<>();
                    while(rs.next()){
                        alUtente.add(new Utente(rs.getString("nome"),rs.getString("cognome"),rs.getString("codicefiscale"),rs.getString("email"),rs.getString("username"),rs.getString("password")));
                    }
                    out.writeObject(alUtente);
                    out.flush();
                }
            }
                    
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}