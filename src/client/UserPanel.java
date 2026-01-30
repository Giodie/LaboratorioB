//Giodi Carolo 758379
package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserPanel {

    public JButton LogButton, RegButton;
    private Proxy proxy;
    
    /**
     * Costruttore della classe UserPanel, utilzzata per effettuare la registrazione o il login.
     */
    public UserPanel(Proxy proxy) {
        this.proxy = proxy;
        JFrame frame = new JFrame("User");
        frame.setSize(400, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        JPanel toolBar = new JPanel();
        toolBar.setBackground(new Color(170, 140, 220));
        toolBar.setPreferredSize(new Dimension(0, 60));
        toolBar.setLayout(new BorderLayout());

        JLabel toolbarLabel = new JLabel("Hai già un account?", SwingConstants.CENTER);
        toolbarLabel.setFont(new Font("Arial", Font.BOLD, 20));
        toolbarLabel.setForeground(Color.WHITE);

        toolBar.add(toolbarLabel, BorderLayout.CENTER);
        frame.add(toolBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

    

        LogButton = new JButton("Log In");
        LogButton.setFont(new Font("Arial", Font.BOLD, 25));
        LogButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        LogButton.setFocusable(false);
        LogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LogInPanel(proxy);
                frame.dispose();
            }
        });

        
        JLabel regLabel = new JLabel("Altrimenti registrati");
        regLabel.setFont(new Font("Arial", Font.BOLD, 18));
        regLabel.setForeground(new Color(170, 140, 220));
        regLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        RegButton = new JButton("Registrati");
        RegButton.setFont(new Font("Arial", Font.BOLD, 25));
        RegButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        RegButton.setFocusable(false);
        RegButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Register(proxy);
                frame.dispose();
            }
        });

        
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(LogButton);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(regLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(RegButton);
        mainPanel.add(Box.createVerticalGlue());

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}

