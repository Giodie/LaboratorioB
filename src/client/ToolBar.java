//Giodi Carolo 758379
package client;
import com.opencsv.exceptions.CsvValidationException;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;

public class ToolBar extends JPanel {

    JFrame utilityFrame;

    JButton UserButton;
    private Proxy proxy;
    
/**
 * Costruttore dell'oggetto ToolBar.
 */
    public ToolBar(Proxy proxy) {
        this.proxy = proxy;
        setLayout(new BorderLayout());
        setBackground(new Color(170, 140, 220));
        setPreferredSize(new Dimension(0, 60));

        JLabel title = new JLabel("LIBRARY SPACE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 30));
        title.setForeground(Color.WHITE);

        UserButton = new JButton("User");
        UserButton.setFont(new Font("Arial", Font.BOLD, 20)); // testo più grande
        UserButton.setFocusable(false);
        UserButton.setPreferredSize(new Dimension(120, 40)); // bottone più grande
        UserButton.addActionListener(e -> {
            UserPanel user = new UserPanel(proxy);
        });

        add(title, BorderLayout.CENTER);
        add(UserButton, BorderLayout.EAST);
    }



}
