/**
 * Course: ITMD 411
 * Date: Dec/ 04/ 2024
 * Done by: Md. Mahmudul Hasan (A20502196)
 * PROJECT: Database Recording
 */

package javaapplication1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

    // Data Access Object for database operations
    Dao dao = new Dao();

    // Flag to check if the user is an admin
    Boolean chkIfAdmin = null;

    // Menu components
    private JMenu mnuFile = new JMenu("File");
    private JMenu mnuAdmin = new JMenu("Admin");
    private JMenu mnuTickets = new JMenu("Tickets");
    private JMenu mnuFilters = new JMenu("Filters");

    JMenuItem mnuItemExit;
    JMenuItem mnuItemUpdateTicket;
    JMenuItem mnuItemDeleteTicket;
    JMenuItem mnuItemCloseTicket;
    JMenuItem mnuItemOpenTicket;
    JMenuItem mnuItemViewTicket;
    JMenuItem mnuItemUpdateUser;
    JMenuItem mnuItemDeleteUser;
    JMenuItem mnuItemAssignTicket;
    JMenuItem mnuItemFilterByStatus;
    JMenuItem mnuItemFilterByPriority;

    // Buttons for ticket operations
    private JButton btnUpdateTicketDescription;
    private JButton btnDeleteTicket;
    private JButton btnCloseTicket;

    // Constructor initializes the GUI based on user role (Admin/User)
    public Tickets(Boolean isAdmin) {
        chkIfAdmin = isAdmin;
        createMenu(); // Create and configure menu components
        prepareGUI(); // Prepare the main GUI layout
    }

    // Method to create and configure the menu
    private void createMenu() {
        // File menu
        mnuItemExit = new JMenuItem("Exit");
        mnuFile.add(mnuItemExit);

        // Admin menu options (only accessible to admin users)
        mnuItemUpdateTicket = new JMenuItem("Update Ticket");
        mnuItemDeleteTicket = new JMenuItem("Delete Ticket");
        mnuItemCloseTicket = new JMenuItem("Close Ticket");
        mnuItemAssignTicket = new JMenuItem("Assign Ticket");
        mnuItemUpdateUser = new JMenuItem("Update User");
        mnuItemDeleteUser = new JMenuItem("Delete User");
        mnuAdmin.add(mnuItemUpdateTicket);
        mnuAdmin.add(mnuItemDeleteTicket);
        mnuAdmin.add(mnuItemCloseTicket);
        mnuAdmin.add(mnuItemAssignTicket);
        mnuAdmin.add(mnuItemUpdateUser);
        mnuAdmin.add(mnuItemDeleteUser);

        // Tickets menu options
        mnuItemOpenTicket = new JMenuItem("Open Ticket");
        mnuItemViewTicket = new JMenuItem("View Tickets");
        mnuTickets.add(mnuItemOpenTicket);
        mnuTickets.add(mnuItemViewTicket);

        // Filters menu options
        mnuItemFilterByStatus = new JMenuItem("Filter by Status");
        mnuItemFilterByPriority = new JMenuItem("Filter by Priority");
        mnuFilters.add(mnuItemFilterByStatus);
        mnuFilters.add(mnuItemFilterByPriority);

        // Add action listeners to menu items
        mnuItemExit.addActionListener(this);
        mnuItemUpdateTicket.addActionListener(this);
        mnuItemDeleteTicket.addActionListener(this);
        mnuItemCloseTicket.addActionListener(this);
        mnuItemAssignTicket.addActionListener(this);
        mnuItemUpdateUser.addActionListener(this);
        mnuItemDeleteUser.addActionListener(this);
        mnuItemOpenTicket.addActionListener(this);
        mnuItemViewTicket.addActionListener(this);
        mnuItemFilterByStatus.addActionListener(this);
        mnuItemFilterByPriority.addActionListener(this);

        // Disable admin menu if the user is not an admin
        if (!chkIfAdmin) {
            mnuAdmin.setEnabled(false);
        }
    }

    // Method to set up the main GUI
    private void prepareGUI() {
        // Configure menu bar
        JMenuBar bar = new JMenuBar();
        bar.add(mnuFile);
        bar.add(mnuAdmin);
        bar.add(mnuTickets);
        bar.add(mnuFilters);
        setJMenuBar(bar);

        // Create button panel for ticket operations
        JPanel buttonPanel = new JPanel();
        btnUpdateTicketDescription = new JButton("Update My Ticket");
        btnDeleteTicket = new JButton("Delete My Ticket");
        btnCloseTicket = new JButton("Close a Ticket (Admin Only)");

        // Add action listeners to buttons
        btnUpdateTicketDescription.addActionListener(this);
        btnDeleteTicket.addActionListener(this);
        btnCloseTicket.addActionListener(this);

        // Add buttons to panel
        buttonPanel.add(btnUpdateTicketDescription);
        buttonPanel.add(btnDeleteTicket);

        if (chkIfAdmin) {
            buttonPanel.add(btnCloseTicket);
        }

        add(buttonPanel, "South");

        // Handle window close event
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent wE) {
                System.exit(0);
            }
        });

        // Configure frame properties
        setSize(600, 600);
        getContentPane().setBackground(Color.LIGHT_GRAY);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Handles menu and button actions
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == mnuItemExit) {
            System.exit(0);
        } else if (e.getSource() == mnuItemOpenTicket) {
            // Open a new ticket
            String ticketName = JOptionPane.showInputDialog(null, "Enter your name");
            String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

            int id = dao.insertTicket(ticketName, ticketDesc);
            if (id != 0) {
                JOptionPane.showMessageDialog(null, "Ticket ID: " + id + " created successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to create the ticket.");
            }

        } else if (e.getSource() == btnUpdateTicketDescription) {
            // Update a ticket description
            try {
                int ticketId = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Ticket ID to update:"));
                String newDescription = JOptionPane.showInputDialog(null, "Enter new description:");

                if (ticketId > 0 && newDescription != null && !newDescription.trim().isEmpty()) {
                    boolean isUpdated = dao.updateTicketById(ticketId, newDescription);
                    if (isUpdated) {
                        JOptionPane.showMessageDialog(null, "Ticket ID: " + ticketId + " updated successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to update Ticket ID: " + ticketId);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid input provided.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid ticket ID.");
            }

        } else if (e.getSource() == btnDeleteTicket) {
            // Delete a ticket
            try {
                int ticketId = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Ticket ID to delete:"));

                if (ticketId > 0) {
                    boolean isDeleted = dao.deleteTicketById(ticketId);
                    if (isDeleted) {
                        JOptionPane.showMessageDialog(null, "Ticket ID: " + ticketId + " deleted successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to delete Ticket ID: " + ticketId);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Ticket ID entered.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid ticket ID.");
            }

        } else if (e.getSource() == btnCloseTicket) {
            // Close a ticket (admin only)
            try {
                int ticketId = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter Ticket ID to close:"));

                if (ticketId > 0) {
                    boolean isClosed = dao.closeTicketById(ticketId);
                    if (isClosed) {
                        JOptionPane.showMessageDialog(null, "Ticket ID: " + ticketId + " closed successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to close Ticket ID: " + ticketId);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Ticket ID entered.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid ticket ID.");
            }

        } else if (e.getSource() == mnuItemViewTicket) {
            // View all tickets in a table
            try {
                JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
                JScrollPane sp = new JScrollPane(jt);
                add(sp);
                setVisible(true);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
    }
}
