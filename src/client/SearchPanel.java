package client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SearchPanel extends JPanel implements ActionListener {

    JTextField titleSearch;
    JTextField authorSearch;
    JTextField yearSearch;
    JButton searchButton;
    CsvTable csvTable;
    /**
     * Costruttore del campo searchPanel, utilizzato per effettuare la ricerca dei libri
     * @param csvTable la tabella su cui effettuare la ricerca.
     */
    public SearchPanel(CsvTable csvTable) {
        this.csvTable = csvTable;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); 

        JLabel titleLabel = new JLabel("Search by title");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); 
        titleSearch = new JTextField(20);
        titleSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25)); 
        titleSearch.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel("Author");
        authorLabel.setFont(new Font("Arial", Font.BOLD, 20));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        authorSearch = new JTextField(20);
        authorSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        authorSearch.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel yearLabel = new JLabel("Year");
        yearLabel.setFont(new Font("Arial", Font.BOLD, 20));
        yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        yearSearch = new JTextField(20);
        yearSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        yearSearch.setAlignmentX(Component.CENTER_ALIGNMENT);

        searchButton = new JButton("Search Book");
        searchButton.setFont(new Font("Arial", Font.BOLD, 18));
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        searchButton.setMaximumSize(new Dimension(200, 40));
        searchButton.addActionListener(this);

       
        add(titleLabel);
        add(Box.createVerticalStrut(5));
        add(titleSearch);
        add(Box.createVerticalStrut(15));
        add(authorLabel);
        add(Box.createVerticalStrut(5));
        add(authorSearch);
        add(Box.createVerticalStrut(15));
        add(yearLabel);
        add(Box.createVerticalStrut(5));
        add(yearSearch);
        add(Box.createVerticalStrut(20));
        add(searchButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String titleFilter = titleSearch.getText();
            String authorFilter = authorSearch.getText();
            String yearFilter = yearSearch.getText();
            
            csvTable.cercaLibro(titleFilter, authorFilter, yearFilter);
        }
    }
}
