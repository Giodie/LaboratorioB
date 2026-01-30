//Giodi Carolo 758379
package client;
import com.opencsv.CSVReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;

public class LogInPanel implements ActionListener {

    JFrame logInFrame;
    private Utente utente = null;

    public JButton logInButton, registerButton, libraryButton;
    public JTextField userField;
    public JPasswordField passwordField;
    public JLabel errorLabel, middleLabel;
    private boolean isLogged;
    private Proxy proxy;
    private Utente u;
    /**
     * Costruttore della classe LoginPanel; inizializza il frame per il login contenente vari campi per effettuare l'operazione di login o per creare un account.
     */
    public LogInPanel(Proxy proxy) {
        this.proxy = proxy;
        logInFrame = new JFrame("Log In / Register");
        logInFrame.setSize(500, 450); // Larghezza x Altezza
        logInFrame.setLocationRelativeTo(null); // centra la finestra
        logInFrame.setResizable(false);
        logInFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        logInFrame.setLayout(new BorderLayout());

        JPanel toolBar = new JPanel();
        toolBar.setBackground(new Color(170, 140, 220));
        toolBar.setPreferredSize(new Dimension(0, 60));
        JLabel title = new JLabel("Hai già un account?", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        toolBar.setLayout(new BorderLayout());
        toolBar.add(title, BorderLayout.CENTER);
        logInFrame.add(toolBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

       
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userField = new JTextField(20);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        logInButton = new JButton("LOG IN");
        logInButton.setFont(new Font("Arial", Font.BOLD, 16));
        logInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logInButton.setMaximumSize(new Dimension(200, 35));

        
        registerButton = new JButton("REGISTRATI");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setMaximumSize(new Dimension(200, 35));
        registerButton.addActionListener(e -> new Register(proxy));

        
        middleLabel = new JLabel("Altrimenti registrati", SwingConstants.CENTER);
        middleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        middleLabel.setForeground(new Color(170, 140, 220));
        middleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        libraryButton = new JButton("LIBRARY");
        libraryButton.setFont(new Font("Arial", Font.BOLD, 16));
        libraryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        libraryButton.setMaximumSize(new Dimension(200, 35));
        libraryButton.addActionListener(e -> generateLibrary());

        
        errorLabel = new JLabel(" ", SwingConstants.CENTER);
        errorLabel.setFont(new Font("Arial", Font.BOLD, 14));
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        
        mainPanel.add(userLabel);
        mainPanel.add(userField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(passwordLabel);
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(logInButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(middleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(registerButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(errorLabel);

        logInFrame.add(mainPanel, BorderLayout.CENTER);

        
        logInButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passwordField.getPassword());
            isLogged = proxy.login(username,password);
            if (isLogged) {
                String cf = proxy.getCodiceFiscale(username,password);
                u = proxy.getUtenteDaCF(cf);
                mainPanel.removeAll();
                mainPanel.add(libraryButton);
                toolBar.removeAll();
                JLabel loggedTitle = new JLabel("Vai alle tue librerie personalizzate", SwingConstants.CENTER);
                loggedTitle.setFont(new Font("Arial", Font.BOLD, 18));
                loggedTitle.setForeground(Color.WHITE);
                toolBar.add(loggedTitle, BorderLayout.CENTER);

                mainPanel.revalidate();
                mainPanel.repaint();
                toolBar.revalidate();
                toolBar.repaint();
            } else {
                errorLabel.setText("Utente o password errati!");
            }
        });

        logInFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Non usato qui, la logica è nei listener
    }
    
    /**
     * Genera il frame del personalLibrary in seguito al login.
     */
    public void generateLibrary() {
        new PersonalLibrary(u,proxy);
    }
}
