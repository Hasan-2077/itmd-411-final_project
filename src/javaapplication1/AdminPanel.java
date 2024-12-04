package javaapplication1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class AdminPanel extends JFrame implements ActionListener {

    Dao dao = new Dao(); // Database access object

    private JButton btnViewTickets;
    private JButton btnViewUsers;
    private JButton btnClosePanel;
    private JButton btnCloseTicket; // New button to close tickets

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
        btnClosePanel = new JButton("Close Panel");
        btnCloseTicket = new JButton("Close a Ticket"); // New button

        // Add action listeners
        btnViewTickets.addActionListener(this);
        btnViewUsers.addActionListener(this);
        btnClosePanel.addActionListener(this);
        btnCloseTicket.addActionListener(this); // Add action listener for new button

        // Add buttons to the panel
        buttonPanel.add(btnViewTickets);
        buttonPanel.add(btnViewUsers);
        buttonPanel.add(btnCloseTicket); // Add the new button to the panel
        buttonPanel.add(btnClosePanel);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnViewTickets) {
            // Load ticket data into the table
            try {
                table.setModel(ticketsJTable.buildTableModel(dao.readRecords()));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load tickets.");
            }
        } else if (e.getSource() == btnViewUsers) {
            // Load user data into the table
            try {
                table.setModel(ticketsJTable.buildTableModel(dao.getAllUsers()));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load users.");
            }
        } else if (e.getSource() == btnClosePanel) {
            // Close the admin panel
            dispose();
        } else if (e.getSource() == btnCloseTicket) {
            // Close a ticket
            try {
                int ticketId = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter Ticket ID to close:"));
                if (ticketId > 0) {
                    boolean isClosed = dao.closeTicketById(ticketId);
                    if (isClosed) {
                        JOptionPane.showMessageDialog(this, "Ticket ID: " + ticketId + " closed successfully!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to close Ticket ID: " + ticketId);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Ticket ID entered.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid ticket ID.");
            }
        }
    }
}
