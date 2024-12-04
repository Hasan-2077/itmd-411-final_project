package javaapplication1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class AdminPanel extends JFrame implements ActionListener {

    Dao dao = new Dao();

    private JButton btnViewTickets;
    private JButton btnViewUsers;
    private JButton btnClose;
    private JTable table;

    public AdminPanel() {
        super("Admin Dashboard");
        prepareGUI();
    }

    private void prepareGUI() {
        // Set up the frame
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.LIGHT_GRAY);

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.DARK_GRAY);
        JLabel headerLabel = new JLabel("Admin Panel - Manage Tickets and Users");
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center panel for the table
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel();
        btnViewTickets = new JButton("View Tickets");
        btnViewUsers = new JButton("View Users");
        btnClose = new JButton("Close Panel");

        btnViewTickets.addActionListener(this);
        btnViewUsers.addActionListener(this);
        btnClose.addActionListener(this);

        buttonPanel.add(btnViewTickets);
        buttonPanel.add(btnViewUsers);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnViewTickets) {
            // Load ticket data into the table
            try {
                ResultSet rs = dao.readRecords();
                table.setModel(ticketsJTable.buildTableModel(rs));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == btnViewUsers) {
            // Load user data into the table
            try {
                ResultSet rs = dao.getAllUsers(); // New method in Dao for fetching user data
                table.setModel(ticketsJTable.buildTableModel(rs));
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == btnClose) {
            // Close the admin panel
            dispose();
        }
    }
}
