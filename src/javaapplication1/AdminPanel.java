/**
 * Course: ITMD 411
 * Date: Dec/ 04/ 2024
 * Done by: Md. Mahmudul Hasan (A20502196)
 * PROJECT: Database Recording
 */

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

    // Data access object for interacting with the database
    Dao dao = new Dao();

    // Buttons for various admin actions
    private JButton btnViewTickets;
    private JButton btnViewUsers;
    private JButton btnClosePanel;
    private JButton btnCloseTicket;

    // Table to display data (tickets/users)
    private JTable table;

    // Constructor for the AdminPanel class, initializes the GUI
    public AdminPanel() {
        super("Admin Dashboard");
        prepareGUI();
    }

    // Prepares the graphical user interface for the admin panel
    private void prepareGUI() {

        // Configure main frame properties
        setSize(800, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.LIGHT_GRAY);

        // Create and add the header section
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.DARK_GRAY);
        JLabel headerLabel = new JLabel("Admin Panel - Manage Tickets and Users");
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Create and add the main table for displaying data
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Create and add the button panel for admin actions
        JPanel buttonPanel = new JPanel();
        btnViewTickets = new JButton("View Tickets");
        btnViewUsers = new JButton("View Users");
        btnClosePanel = new JButton("Close Panel");
        btnCloseTicket = new JButton("Close a Ticket");

        // Register button click listeners
        btnViewTickets.addActionListener(this);
        btnViewUsers.addActionListener(this);
        btnClosePanel.addActionListener(this);
        btnCloseTicket.addActionListener(this);

        // Add buttons to the button panel
        buttonPanel.add(btnViewTickets);
        buttonPanel.add(btnViewUsers);
        buttonPanel.add(btnCloseTicket);
        buttonPanel.add(btnClosePanel);
        add(buttonPanel, BorderLayout.SOUTH);

        // Configure the default behavior of the frame
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    // Handles button click events for admin actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnViewTickets) {
            // Fetches and displays all tickets in the table
            try {
                table.setModel(ticketsJTable.buildTableModel(dao.readRecords()));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load tickets.");
            }
        } else if (e.getSource() == btnViewUsers) {
            // Fetches and displays all users in the table
            try {
                table.setModel(ticketsJTable.buildTableModel(dao.getAllUsers()));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load users.");
            }
        } else if (e.getSource() == btnClosePanel) {
            // Closes the admin panel
            dispose();
        } else if (e.getSource() == btnCloseTicket) {
            // Prompts the admin to enter a ticket ID and closes the ticket if valid
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
                // Handles invalid ticket ID input
                JOptionPane.showMessageDialog(this, "Please enter a valid ticket ID.");
            }
        }
    }
}
