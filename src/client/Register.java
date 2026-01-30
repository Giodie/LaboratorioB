//Giodi Carolo 758379
package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.util.ArrayList;

public class Register implements ActionListener {

    JFrame regFrame;
    JButton registerButton;
    JButton libraryButton;

    JTextField nameField, cognomeField, emailField, FiscalNumberField, userIDField;
    JPasswordField passwordField;
    JLabel statusLabel;
    JLabel title;

    JPanel toolBar;
    JPanel mainPanel;

    private String nome;
    private String cognome;
    private Utente utente;
    private Proxy proxy;
    private ArrayList<Utente> listaUtenti;
    /**
     * Costruttore della classe Register. Inizializza il frame sulla quale è possibile effettuare la registrazione. effettua il controllo di vari campi per confermare che non ci siano errori o duplicati.
     */
    public Register(Proxy proxy) {  
        this.proxy = proxy;
        listaUtenti = proxy.getTuttiUtenti();
        
        regFrame = new JFrame("Register");
        regFrame.setSize(500, 600);
        regFrame.setLocationRelativeTo(null);
        regFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        regFrame.setLayout(new BorderLayout());

        toolBar = new JPanel();
        toolBar.setBackground(new Color(170, 140, 220));
        toolBar.setPreferredSize(new Dimension(0, 60));
        toolBar.setLayout(new BorderLayout());

        title = new JLabel("Registrati per maggiori opzioni", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        toolBar.add(title, BorderLayout.CENTER);
        regFrame.add(toolBar, BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JLabel nameLabel = new JLabel("Nome:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameField = new JTextField(20);

        JLabel cognomeLabel = new JLabel("Cognome:");
        cognomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        cognomeField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 16));
        emailField = new JTextField(20);

        JLabel fiscalLabel = new JLabel("Codice fiscale:");
        fiscalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        FiscalNumberField = new JTextField(20);

        JLabel userIDLabel = new JLabel("Username:");
        userIDLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userIDField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passwordField = new JPasswordField(20);

        registerButton = new JButton("REGISTRAZIONE");
        registerButton.setFont(new Font("Arial", Font.BOLD, 18));
        registerButton.addActionListener(this);

        statusLabel = new JLabel(" ");

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(cognomeLabel);
        mainPanel.add(cognomeField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(emailLabel);
        mainPanel.add(emailField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(fiscalLabel);
        mainPanel.add(FiscalNumberField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(userIDLabel);
        mainPanel.add(userIDField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(registerButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(statusLabel);

        regFrame.add(mainPanel, BorderLayout.CENTER);
        regFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == registerButton) {
            try {
                registrazione();
            } catch (IOException | CsvValidationException ex) {
                statusLabel.setText("Errore durante la registrazione");
            }
        }

    }

    public void registrazione() throws IOException, CsvValidationException {

        nome = nameField.getText();
        cognome = cognomeField.getText();
        String email = emailField.getText();
        String codiceFiscale = FiscalNumberField.getText();
        String username = userIDField.getText();
        String password = new String(passwordField.getPassword());

        if (stringheVuote(nome, cognome, codiceFiscale, email, username, password)) {
            JOptionPane.showMessageDialog(regFrame, "I campi non possono essere vuoti");
            return;
        }

        if (!controlloEmail(email)) {
            JOptionPane.showMessageDialog(regFrame, "Email non valida!");
            return;
        }

        if (!controlloCF(codiceFiscale)) {
            JOptionPane.showMessageDialog(regFrame, "Codice Fiscale errato");
            return;
        }

        if (emailEsistente(email)) {
            JOptionPane.showMessageDialog(regFrame, "Email già esistente");
            return;
        }
        if (cfEsistente(codiceFiscale)) {
            JOptionPane.showMessageDialog(regFrame, "Codice Fiscale già esistente");
            return;
        }

        if (usernameEsistente(username)) {
            JOptionPane.showMessageDialog(regFrame, "Username già esistente");
            return;
        }


        utente = new Utente(nome,cognome,codiceFiscale,email,username,password);
        proxy.register(utente);
        JOptionPane.showMessageDialog(regFrame, "Registrato correttamente!");

       
        mostraAccessoLibreria();
    }
    /**
     * Permette ad un utente appena registrato di accedere alla schermata delle librerie
     */
    private void mostraAccessoLibreria() {

        mainPanel.removeAll();

        title.setText("Vai alle tue librerie");

        libraryButton = new JButton("LIBRARY");
        libraryButton.setFont(new Font("Arial", Font.BOLD, 24));
        libraryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        libraryButton.addActionListener(this);

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(libraryButton);
        mainPanel.add(Box.createVerticalGlue());
        libraryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new PersonalLibrary(utente,proxy);
            }
        });

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Controlla se i campi hanno delle stringhe vuote
     * @param nome il campo nome
     * @param cognome il campo cognome
     * @param codiceFiscale il campo codiceFiscale
     * @param email il campo email
     * @param username il campo username
     * @param password il campo password
     * @return
     */
    private boolean stringheVuote(String nome, String cognome, String codiceFiscale,
                                  String email, String username, String password) {
        return nome.length() == 0 || cognome.length() == 0 ||
                codiceFiscale.length() == 0 || email.length() == 0 ||
                username.length() == 0 || password.length() == 0;
    }
    /**
     * Controlla se l'email è scritta correttamente
     * @param email l'email da controlalre
     * @return true se è scritta correttamente false altrimenti
     */
    private boolean controlloEmail(String email) {
        int chiocciola = email.indexOf('@');
        int punto = email.lastIndexOf('.');
        return chiocciola > 0 && punto > chiocciola;
    }
    /**
     * Controlla se esiste un utente con la stessa email
     * @param email l'email su cui effettuare il controllo
     * @return true se esiste false altirmenti
     */
    private boolean emailEsistente(String email) {
       for(int i=0;i<listaUtenti.size();i++){
           if(listaUtenti.get(i).getEmail().equals(email)){
               return true;
           }
       }
       return false;
    }
    /**
     * Controlla se esiste un utente con certo username
     * @param username lo username da controllare
     * @return true se esiste false altrimenti
     */
    private boolean usernameEsistente(String username) {
        for(int i=0;i<listaUtenti.size();i++){
            if(listaUtenti.get(i).getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }
    /**
     * Controlla se il codiceFiscale inserito è corretto
     * @param cf il codice su cui effettuare il controllo
     * @return true se il codice fiscale è corretto false altrimenti.
     */
    private boolean controlloCF(String cf){
        String cfnome ="";
        String cfcognome ="";
        String vocali="aeiouAEIOU";
        char c;

        for(int i=0;i<nome.length();i++){
            c=nome.charAt(i);
            if(!(vocali.contains(String.valueOf(c)))&&cfnome.length()<3){
                cfnome=cfnome+c;
            }
        }

        if(cfnome.length()<3){
            for(int i=0;i<nome.length();i++){
                c=nome.charAt(i);
                if((vocali.contains(String.valueOf(c)))) {
                    cfnome = cfnome+c;
                }
                if(cfnome.length()==3) break;
            }
            while(cfnome.length()<3){
                cfnome=cfnome+"X";
            }
        }

        for(int i=0;i<cognome.length();i++){
            c=cognome.charAt(i);
            if(!(vocali.contains(String.valueOf(c)))&&cfcognome.length()<3){
                cfcognome=cfcognome+c;
            }
        }

        if(cfcognome.length()<3){
            for(int i=0;i<cognome.length();i++){
                c=cognome.charAt(i);
                if((vocali.contains(String.valueOf(c)))) {
                    cfcognome = cfcognome+c;
                }
                if(cfcognome.length()==3) break;
            }
            while(cfcognome.length()<3){
                cfcognome=cfcognome+"X";
            }
        }

        if(cf.length()==16){
            String stringaTagliata=cf.substring(0,6);
            boolean corretto=true;

            if(!stringaTagliata.toUpperCase().equals(
                    cfcognome.toUpperCase() + cfnome.toUpperCase())){
                corretto=false;
            }

            stringaTagliata=cf.substring(6,8);
            for(int i=0;i<stringaTagliata.length() && corretto;i++){
                if(!(Character.isDigit(stringaTagliata.charAt(i)))){
                    corretto=false;
                }
            }

            if(corretto && !(Character.isLetter(cf.charAt(8)))){
                corretto=false;
            }

            stringaTagliata=cf.substring(9, 11);
            for(int i=0;i<stringaTagliata.length() && corretto;i++){
                if(!(Character.isDigit(stringaTagliata.charAt(i)))){
                    corretto=false;
                }
            }

            if(corretto && !(Character.isLetter(cf.charAt(11)))){
                corretto=false;
            }

            stringaTagliata=cf.substring(12,15);
            for(int i=0;i<stringaTagliata.length() && corretto;i++){
                if(!(Character.isDigit(stringaTagliata.charAt(i)))){
                    corretto=false;
                }
            }

            if(corretto && !(Character.isLetter(cf.charAt(15)))){
                corretto=false;
            }

            return corretto;
        }
        return false;
    }
    /**
     * Controlla se c'è un utente con lo stesso codice fiscale.
     * @param cf il codice fiscale su cui eseguire il controllo
     * @return true se c'è un duplicato false altrimenti.
     */
    private boolean cfEsistente(String cf) {
        for(int i=0;i<listaUtenti.size();i++){
            if(listaUtenti.get(i).getCF().equals(cf)){
                return true;
            }
        }
        return false;
    }

}

